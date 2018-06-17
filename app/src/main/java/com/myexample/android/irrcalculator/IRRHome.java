package com.myexample.android.irrcalculator;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class IRRHome extends AppCompatActivity {
    EditText periodOfMonthsEditText,priceEditText,interestRateEditText,processingFeesEditText,advacneInstallmentsEditText;
    String installmentText;
    double installmentsCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrhome);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final TextView showIRR = (TextView) findViewById(R.id.IRRView);
        final TextView showInstallment = (TextView) findViewById(R.id.installmentView);
        final Button calculateButton = (Button) findViewById(R.id.calculateBut);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUserFields()){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                    showIRR.setText(calculateIRR() + "%");
                    showInstallment.setText(installmentText+" @"+ new DecimalFormat("##").format(installmentsCount));

                }
            }
        });

    }

    /**
     * To validate all fields are entered or not
     * @return boolean flag
     */
    boolean validateUserFields(){
        periodOfMonthsEditText = (EditText) findViewById(R.id.monthsPeriodText);
        priceEditText = (EditText) findViewById(R.id.priceText);
        interestRateEditText = (EditText) findViewById(R.id.interestRateText);
        processingFeesEditText = (EditText) findViewById(R.id.processingFeesText);
        advacneInstallmentsEditText = (EditText) findViewById(R.id.advanceInstallmentsText);
        boolean flag = false;
        if(periodOfMonthsEditText.getText().length()==0){
            periodOfMonthsEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(periodOfMonthsEditText, InputMethodManager.SHOW_IMPLICIT);
        } else if(priceEditText.getText().length()==0){
            priceEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(priceEditText, InputMethodManager.SHOW_IMPLICIT);
        } else if(interestRateEditText.getText().length()==0){
            interestRateEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(interestRateEditText,InputMethodManager.SHOW_IMPLICIT);
        } else if(processingFeesEditText.getText().length()==0){
            processingFeesEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(processingFeesEditText,InputMethodManager.SHOW_IMPLICIT);
        } else if(advacneInstallmentsEditText.getText().length()==0){
            advacneInstallmentsEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(advacneInstallmentsEditText,InputMethodManager.SHOW_IMPLICIT);
        } else{
            flag = true;
        }
        if(!flag)
            toastMessage("Enter all Fields!");
        return  flag;
    }

    /**
     * To calculate IRR percentage
     * @return String
     */
    String calculateIRR(){

        AssetManager asset = getAssets();
        try{
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(asset.open("Book1.xls"));
            HSSFWorkbook wb = new HSSFWorkbook(poifsFileSystem);
            Sheet sh = wb.getSheetAt(0);
            setCellDoubleValue(sh,1,1,getPeriodOfMonthsEditText());
            setCellDoubleValue(sh,2,1,getPriceEditText());
            setCellDoubleValue(sh,3,1,(getInterestRateEditText()/100.0));
            setCellDoubleValue(sh,6,1,getProcessingFeesEditText());
            setCellDoubleValue(sh, 7, 1, getAdvacneInstallmentsEditText());
            HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
            int i = 24;
            installmentsCount = 0;
            while(sh.getRow(i).getCell(1).getNumericCellValue()>0){
                installmentsCount+=1;
                i++;
            }
            Row row = sh.getRow(12);
            Cell cell = row.getCell(1);
            installmentText = new DecimalFormat("##").format(cell.getNumericCellValue());
            row = sh.getRow(0);
            cell = row.getCell(1);
            DecimalFormat df = new DecimalFormat("##.00");
            return df.format(cell.getNumericCellValue()*100);

        }catch (Exception e){
            e.printStackTrace();

        }
        installmentText = "";
        return "NaN";
    }


    /**
     *
     * @param sh Working Sheet
     * @param rowindex Row to write
     * @param colIndex Column to Write
     * @param value Value for cell
     */
    void setCellDoubleValue(Sheet sh, int rowindex, int colIndex,double value){
        Row row = sh.getRow(rowindex);
        Cell cell = row.getCell(colIndex);
        cell.setCellValue(value);
    }


    /**
     * Toast Message
     * @param message
     */
    void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public double getPeriodOfMonthsEditText() {
        periodOfMonthsEditText = (EditText) findViewById(R.id.monthsPeriodText);
        double periodMonths = Double.parseDouble(periodOfMonthsEditText.getText().toString());
        return periodMonths;
    }

    public double getPriceEditText() {

        priceEditText = (EditText) findViewById(R.id.priceText);
        double price = Double.parseDouble(priceEditText.getText().toString());
        return price;
    }

    public double getInterestRateEditText() {
        interestRateEditText = (EditText) findViewById(R.id.interestRateText);
        double interestRate = Double.parseDouble(interestRateEditText.getText().toString());
        return interestRate;

    }

    public double getProcessingFeesEditText() {
        processingFeesEditText = (EditText) findViewById(R.id.processingFeesText);
        double processingFees = Double.parseDouble(processingFeesEditText.getText().toString());
        return processingFees;

    }

    public double getAdvacneInstallmentsEditText() {
        advacneInstallmentsEditText = (EditText) findViewById(R.id.advanceInstallmentsText);
        double advanceInstallments = Double.parseDouble(advacneInstallmentsEditText.getText().toString());
        return advanceInstallments;

    }
}
