package blocker.com.newalarmservice.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import blocker.com.newalarmservice.utilities.MyApplication;


public class MSharedPreferance {
    private List<String> listObserver;
    private static MSharedPreferance sharedPreferance;
    private String spName = "duecategory";
    private SharedPreferences sp;

    private String categoryStr = "";

    private MSharedPreferance() {
        listObserver = new ArrayList<>();
        sp = MyApplication.getApplicationInstance().getMyApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);

        if (sp.getString("category", "").length() == 0) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("category", "Accommodation,Credit cards,Mobile Recharge,Electricity,DTH,Education,Car,Two wheeler,Food,Gifts" +
                    ",Insurance,Medicare,Pets,Sports,Shopping,Tax,Vacation,Investment,Saving,Child care,Liquor,Cigarette,Landline,Other");
            editor.apply();
        }

    }


    public static MSharedPreferance getSharedPreferance() {
        if (sharedPreferance == null)
            sharedPreferance = new MSharedPreferance();
        return sharedPreferance;
    }

    public String[] getSPCategoryList() {
        String category = sp.getString("category", "");
        if (category != null) {
            categoryStr = category;
            if (category.length() > 0) {
                String[] categoryArray = category.split(",");
                Log.e("category item", categoryArray[2]);
                return categoryArray;
            }
        }
        return null;
    }

    public void addCategory(String categoryName) {
        String addCategoryString = categoryName + "," + categoryStr;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("category", addCategoryString);
        editor.apply();

    }

    public void deleteCategory(String categoryName) {
        boolean flag = true;
        StringBuilder stringBuilder = new StringBuilder();
        // categoryStr.replace(",Accommodation","");
        String[] arr = sp.getString("category", "").split(",");
        for (int i = 0; i < arr.length; i++) {

            if (!(arr[i].equalsIgnoreCase(categoryName))) {
                if (flag) {
                    stringBuilder.append(arr[i]);
                    flag = false;
                } else
                    stringBuilder.append(",").append(arr[i]);
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("category", stringBuilder.toString());
        editor.apply();
    }
}
