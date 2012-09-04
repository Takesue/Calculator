package sample.application.calculator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorActivity extends Activity {

	// 押された演算キーを格納する領域	
	public String funcKey = null;

	// 表示されていた数字を格納する領域
	public String preNumber = null;

	public String strTemp = "";
	public Integer operator = 0;
	public String strResult = "0";
	
	public static final int SS = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_calculator);
		this.readPreferences();
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		this.writePreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.activity_calculator, menu);
		return true;
	}

	public void numKeyOnClic(View v) {
		String strInkey = (String)((Button)v).getText();

		if (".".equals(strInkey)) {
			if (this.strTemp.length() == 0) {
				this.strTemp = "0.";
			}
			else {
				this.strTemp = this.strTemp + ".";
			}
		}
		else {
			this.strTemp = this.strTemp + strInkey;
		}
		this.showNumber(this.strTemp);

	}

	private void showNumber(String strNum) {
		DecimalFormat form = new DecimalFormat("#,##0");
		String strDecimal = "";
		String strInt = "";
		String fText = "";
		
		if (strNum.length() > 0) {
			int ecimalpoint = strNum.indexOf(".");
			if (ecimalpoint > -1) {
				strDecimal = strNum.substring(ecimalpoint);
				strInt = strNum.substring(0, ecimalpoint);
			}
			else {
				strInt = strNum;
			}
			fText = form.format(Double.parseDouble(strInt)) + strDecimal;
		}
		else {
			fText = "0";
		}
		((TextView)this.findViewById(R.id.displayPanel)).setText(fText);
	}

//	public void numKeyOnClic(View v) {
//		Button button = (Button) v;
//		TextView tv = (TextView) this.findViewById(R.id.displayPanel);
//
//		Log.d(this.getLocalClassName(), "押されたキーは" + button.getText().toString()
//				+ "です");
//		Log.d(this.getLocalClassName(), "[tvインスタンスの確認]" + tv.getClass());
//
//		if (tv.getText().toString().equals("0")) {
//			// 　数字キーが押された場合に、表示が０の場合、０をクリア
//			tv.setText("");
//		}
//		
//		// 数字を追加
//		tv.setText(tv.getText().toString() + button.getText().toString());
//	}

	public void addKeyOnClic(View v) {
		
		// 押された演算キーを格納する
		Button button = (Button) v;
		funcKey = button.getText().toString();
		Log.d(this.getLocalClassName(), "[保存した演算キーの確認]" + funcKey);
		
		// 表示中の文字を取得
		TextView tv = (TextView) this.findViewById(R.id.displayPanel);
		
		// 数字文字列をString型として格納
		preNumber = tv.getText().toString();
		Log.d(this.getLocalClassName(), "[保存した数字文字列の確認]" + preNumber);
		
		// 表示を０に戻す
		tv.setText("0");
	}
	
//	public void equalKeyOnClic(View v) {
//
//		// 表示中の文字を取得
//		TextView tv = (TextView) this.findViewById(R.id.displayPanel);
//		
//		if ((preNumber != null) && (funcKey != null)) {
//			
//			// Integer型に変換
//			Integer i_preNumber = Integer.parseInt(preNumber);
//			
//			// Integer型に変換
//			String nextNumber = tv.getText().toString();
//			Integer i_nextNumber = Integer.parseInt(nextNumber);
//
//			if (funcKey.equals("+")) {
//				Integer i = i_preNumber + i_nextNumber;
//				Log.d(this.getLocalClassName(), "[加算結果の確認]" + i.toString());
//				tv.setText(i.toString());
//			}
//			else if (funcKey.equals("-")) {
//			}
//			else if (funcKey.equals("*")) {
//			}
//			else if (funcKey.equals("/")) {
//			}
//		}
//
//		// 演算終了後は値をクリアします
//		preNumber = null;
//		funcKey = null;
//	}

	public void operatorKeyOnClick(View v) {
		if (this.operator != 0) {
			if (this.strTemp.length() > 0) {
				this.strResult = this.doCalc();
				this.showNumber(this.strResult);
			}
		}
		else {
			if (this.strTemp.length() > 0) {
				this.strResult = this.strTemp;
			}
		}
		this.strTemp = "";
		
		if (v.getId() == R.id.keypadEq) {
			this.operator = 0;
		}
		else {
			this.operator = v.getId();
		}
	}


	private String doCalc() {
		BigDecimal bd1 = new BigDecimal(this.strResult);
		BigDecimal bd2 = new BigDecimal(this.strTemp);
		BigDecimal result = BigDecimal.ZERO;
		
		switch (this.operator) {
		case R.id.keypadAdd:
			result = bd1.add(bd2);
			break;
		case R.id.keypadSub:
			result = bd1.subtract(bd2);
			break;
		case R.id.keypadMulti:
			result = bd1.multiply(bd2);
			break;
		case R.id.keypadDiv:
			if (!bd2.equals(BigDecimal.ZERO)) {
				result = bd1.divide(bd2, 12, 3);
			}
			else {
				Toast toast = Toast.makeText(this, R.string.toast_div_by_zero, 1000);
				toast.show();
			}
			break;
		}
		
		if (result.toString().indexOf(".") >= 0) {
			return result.toString().replaceAll("¥¥.0+$|0+$", "");
		}
		else {
			return result.toString();
		}
	}
	
	public void writePreferences() {
		SharedPreferences.Editor editor = this.getSharedPreferences("CalcPrefs", MODE_PRIVATE).edit();
		editor.putString("strTemp", this.strTemp);
		editor.putString("strResult", this.strResult);
		editor.putInt("operator", this.operator);
		editor.putString("strDisplay", 
				((TextView)this.findViewById(R.id.displayPanel)).getText().toString());
	}
	
	public void readPreferences() {
		SharedPreferences prefs = this.getSharedPreferences("CalcPrefs", MODE_PRIVATE);
		this.strTemp = prefs.getString("strTemp", "");
		this.strResult = prefs.getString("strResult", "0");
		this.operator = prefs.getInt("operator", 0);
		((TextView)this.findViewById(R.id.displayPanel)).setText(prefs.getString("strDisplay", ""));
	}
}
