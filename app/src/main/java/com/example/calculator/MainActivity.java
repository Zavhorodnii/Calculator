package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.StorageStats;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import static android.renderscript.ScriptIntrinsicBLAS.RIGHT;
import static com.example.calculator.R.*;

public class MainActivity extends AppCompatActivity {

    public class BlockCalculate{
        ArrayList<TextView> textViewArrayList = new ArrayList<TextView>();
        ArrayList<String> operationSign = new ArrayList<String>();
        ArrayList<String> resultList = new ArrayList<String>();
    }
    ArrayList<BlockCalculate> blockCalculateArrayList = new ArrayList<BlockCalculate>();
    BlockCalculate blockCalculate;
    String operand_1 = "0", operand_2 = "0";
    Float resultCalculate = 0.f;
    Boolean createNewBlock = true;
    String sign = "";


    LinearLayout linearLayout;
    ScrollView scrollView;
    TextView calculateTextView;
    TextView resultTextView;
    Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        linearLayout = (LinearLayout) findViewById(id.linearLayout);
        scrollView = (ScrollView) findViewById(id.scrollView);
        calculateTextView = (TextView) findViewById(R.id.calculateTextView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        clearButton = (Button) findViewById(id.clearButton);
    }

    public void createblockCalculate(){
        blockCalculate = new BlockCalculate();
    }

    public void createLine(){
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0xFFE56501);
        LinearLayout.LayoutParams leyoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 3
        );
        textView.setLayoutParams(leyoutParams);
        linearLayout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        blockCalculate.textViewArrayList.add(textView);
    }

    public void CreateTextView(String buttonText){
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(0,0,50,0);
        if (sign.equals("=")){
            textView.setTextColor(0xFFE56501);
            textView.setTextSize(35);
        } else {
            textView.setTextColor(0xff5c6bc0);
            textView.setTextSize(30);
        }
        LinearLayout.LayoutParams leyoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(leyoutParams);
        textView.setText(buttonText);
        linearLayout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        blockCalculate.textViewArrayList.add(textView);
    }

    public void onNumberClick(View view){
        clearButton.setText("C");
        Button button = (Button) view;
        if(createNewBlock){
            if (sign.equals("=")){
                sign = "";
                CreateTextView((String) calculateTextView.getText());
                sign = "=";
                CreateTextView("= " + resultCalculate);
                createLine();
                blockCalculateArrayList.add(blockCalculate);
                calculateTextView.setText("0");
                resultCalculate = 0.f;
                resultTextView.setText("");
                sign = "";

                calculateTextView.setTextSize(50);
                resultTextView.setTextSize(25);
                resultTextView.setTextColor(0xFFE56501);
            }
            createblockCalculate();
            if (button.getText().equals(".")) {
                calculateTextView.setText("0.");
                operand_1 = "0.";
                createNewBlock = false;
                return;
            } else {
                calculateTextView.setText(button.getText());
                operand_1 = (String) button.getText();
            }
            createNewBlock = false;
        }else {
            if (calculateTextView.getText().equals("0")) {
                if (button.getText().equals(".")) {
                    calculateTextView.setText("0.");
                    operand_1 = "0.";
                    createNewBlock = false;
                    return;
                } else {
                    operand_1 = (String) button.getText();
                    calculateTextView.setText(button.getText());
                }
            }
            else {
                operand_1 += (String) button.getText();
                calculateTextView.setText((String) calculateTextView.getText() + (String) button.getText());
            }
        }
        calculate();
    }

    public void onOperationClick(View view){
        Button button = (Button) view;
        String operation = (String) button.getText();
        if(createNewBlock && sign.equals("=")){
            sign = "";
            CreateTextView((String) calculateTextView.getText());
            sign = "=";
            CreateTextView("= " + resultCalculate);
            createLine();
            blockCalculateArrayList.add(blockCalculate);
            calculateTextView.setText(resultCalculate.toString());
            operand_1 = resultCalculate.toString();
            operand_2 = "0";
            resultTextView.setText(resultCalculate.toString());
            sign = "";

            calculateTextView.setTextSize(50);
            resultTextView.setTextSize(25);
            resultTextView.setTextColor(0xFFE56501);
            createNewBlock = false;
            createblockCalculate();
        }

        if (operation.equals("+") || operation.equals("-") || operation.equals("*") || operation.equals("/") || operation.equals("%") || operation.equals("^")){
            clearButton.setText("C");
            sign = operation;
            String lastOperation = (String) calculateTextView.getText();
            if ((lastOperation.equals("+ ") || lastOperation.equals("- ") || lastOperation.equals("* ") ||
                    lastOperation.equals("/ ") || lastOperation.equals("% ") || lastOperation.equals("^ "))){
                calculateTextView.setText(operation + " ");
                blockCalculate.operationSign.set(blockCalculate.operationSign.size()-1, operation);
            }else {
                if (operand_2.equals("0"))
                    operand_2 = operand_1;
                else
                    operand_2 = resultCalculate.toString();
                operand_1 = "";
                blockCalculate.resultList.add(resultCalculate.toString());
                blockCalculate.operationSign.add(operation);
                CreateTextView((String) calculateTextView.getText());
                calculateTextView.setText(operation + " ");
            }
        }
        else
            switch (operation){
                case "D":
                    if (sign.equals("=") || calculateTextView.getText().equals("0"))
                        break;
                    if (((String)calculateTextView.getText()).length() > 0) {
                        if (!sign.equals("")){
                            if (((String)calculateTextView.getText()).length() > 2) {
                                System.out.println("> 2");
                                String deletelastSimbol = (String) calculateTextView.getText();
                                calculateTextView.setText(deletelastSimbol.substring(0, deletelastSimbol.length() - 1));
                                if(operand_1.length() > 1) {
                                    System.out.println("> 0");
                                    System.out.println("operand_1== " + operand_1);
                                    operand_1 = operand_1.substring(0, operand_1.length() - 1);
                                } else {
                                    System.out.println("== 0");
                                    operand_1 = "0";
                                }
                            }else {
                                if (blockCalculate.textViewArrayList.size() > 0) {
                                    String text = (String) blockCalculate.textViewArrayList.get(blockCalculate.textViewArrayList.size() - 1).getText();
                                    calculateTextView.setText(text);
                                    if (blockCalculate.textViewArrayList.size() > 1) {
                                        String spl[] = text.split(" ");
                                        operand_2 = blockCalculate.resultList.get(blockCalculate.resultList.size()-2);
                                        operand_1 = spl[1];
                                        blockCalculate.resultList.remove(blockCalculate.resultList.size()-1);
                                    } else
                                        operand_1 = text;
                                    linearLayout.removeView(blockCalculate.textViewArrayList.get(blockCalculate.textViewArrayList.size() - 1));
                                    blockCalculate.textViewArrayList.remove(blockCalculate.textViewArrayList.size() - 1);
                                }
                                if (blockCalculate.operationSign.size()> 1) {
                                    sign = blockCalculate.operationSign.get(blockCalculate.operationSign.size() - 2);
                                    blockCalculate.operationSign.remove(blockCalculate.operationSign.size() - 1);
                                } else {
                                    blockCalculate.operationSign.clear();
                                    sign = "";
                                    operand_1 = (String) calculateTextView.getText();
                                    operand_2 = "0";
                                }
                            }
                        }else {
                            String deletelastSimbol = (String) calculateTextView.getText();
                            calculateTextView.setText(deletelastSimbol.substring(0, deletelastSimbol.length() - 1));
                            if(operand_1.length() > 0)
                                operand_1 = operand_1.substring(0, operand_1.length() - 1);
                            if (operand_1.length() == 0) {
                                calculateTextView.setText("0");
                                operand_1 = "0";
                            }
                        }
                    }else {
                        calculateTextView.setText("0");
                    }
                     calculate();
                    break;
                case "C":
                    try {
                        int lenBlockCalculate = blockCalculate.textViewArrayList.size();
                        if (lenBlockCalculate > 0){
                            for (int i = lenBlockCalculate-1; i >=0; i--){
                                linearLayout.removeView(blockCalculate.textViewArrayList.get(i));
                            }
                        }
                        blockCalculate.textViewArrayList.clear();
                        blockCalculate.operationSign.clear();
                        blockCalculate.resultList.clear();
                        operand_1 = operand_2 = "0";
                        clearButton.setText("CE");
                        calculateTextView.setText("0");
                        resultCalculate = 0.f;
                        sign="";
                        resultTextView.setText("");


                        calculateTextView.setTextSize(50);
                        resultTextView.setTextSize(25);
                        resultTextView.setTextColor(0xFFE56501);
                    }catch (Exception ex) { return; }

                    break;
                case "CE":
                    try{
                        linearLayout.removeAllViews();
                        blockCalculateArrayList.clear();
                        blockCalculate.textViewArrayList.clear();
                        createNewBlock = true;
                    } catch (Exception ex){}
                    break;
                case "=":
                    if (((String)clearButton.getText()).equals("C")){

                        calculateTextView.setTextSize(30);
                        resultTextView.setTextSize(40);
                        resultTextView.setTextColor(0xFFE56501);

                        createNewBlock = true;
                        operand_1 = "0";
                        operand_2 = "0";
                        sign="=";
                    }
                    break;
        }
    }

    public void calculate(){
        Float floatOperand_1 = Float.parseFloat(operand_2);
        Float floatOperand_2 = Float.parseFloat(operand_1);
        switch (sign){
            case "+":
                resultCalculate = floatOperand_1 + floatOperand_2;
                resultTextView.setText(resultCalculate.toString());
                break;
            case "-":
                resultCalculate = floatOperand_1 - floatOperand_2;
                resultTextView.setText(resultCalculate.toString());
                break;
            case "*":
                String spl[] = ((String)calculateTextView.getText()).split(" ");
                if (spl.length > 1) {
                    resultCalculate = floatOperand_1 * floatOperand_2;
                    resultTextView.setText(resultCalculate.toString());
                } else {
                    resultTextView.setText(floatOperand_1.toString());
                }
                break;
            case "/":
                String sp2[] = ((String)calculateTextView.getText()).split(" ");
                if (sp2.length > 1) {
                    resultCalculate = floatOperand_1 / floatOperand_2;
                    resultTextView.setText(resultCalculate.toString());
                } else {
                    resultTextView.setText(floatOperand_1.toString());
                }
                break;
            case "%":
                String sp3[] = ((String)calculateTextView.getText()).split(" ");
                if (sp3.length > 1) {
                    resultCalculate = floatOperand_1 % floatOperand_2;
                    resultTextView.setText(resultCalculate.toString());
                } else {
                    resultTextView.setText(floatOperand_1.toString());
                }
                break;
            case "^":
                String sp_pow[] = ((String)calculateTextView.getText()).split(" ");
                if (sp_pow.length > 1) {
                    resultCalculate = (float) Math.pow((double) floatOperand_1, (double) floatOperand_2);
                    resultTextView.setText(resultCalculate.toString());
                } else {
                    resultTextView.setText(floatOperand_1.toString());
                }
                break;
            case "":
                resultCalculate = floatOperand_1 + floatOperand_2;
                if (calculateTextView.getText().equals("0")) {
                    clearButton.setText("CE");
                    resultTextView.setText("");
                } else
                    resultTextView.setText(resultCalculate.toString());
                break;
        }
    }
}
