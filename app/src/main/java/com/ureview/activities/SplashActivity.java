package com.ureview.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.fragments.LoginFragment;
import com.ureview.fragments.SplashFragment;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout relTopBar;
    public CustomTextView txtTitle, txtRight;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColorToAppColorLight();
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
        setContentView(R.layout.activity_splash);
        initComps();
        checkInternetConnectionAndProceed();
    }

    private void initComps() {
        relTopBar = findViewById(R.id.relTopBar);
        txtTitle = findViewById(R.id.txtTitle);
        txtRight = findViewById(R.id.txtRight);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        txtRight.setOnClickListener(this);
    }

    public void setTopBar(String screen) {
        relTopBar.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        txtRight.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
        switch (screen) {
            case "Signup1Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Sign Up");
                break;
            case "Signup3Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Congratulations");
                break;
            case "LoginFragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Login");
                break;
            default:
                txtTitle.setText("Sign Up");
                txtRight.setText("Log In");
                break;
        }
    }

    private void checkInternetConnectionAndProceed() {
        StaticUtils.getHeightAndWidth(this);
        if (StaticUtils.checkInternetConnection(this)) {
            proceedWithFlow();
        } else {
            DialogUtils.showSimpleDialog(this, "", "No Internet Connection", "Retry", "Close", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkInternetConnectionAndProceed();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }, true, false);
        }
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash Key: ", hashKey + "");
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }

    private void proceedWithFlow() {
        printHashKey();
//        check(new String[]{"10", "joe", "mary", "joe", "james", "james", "james", "mary", "mary"});
        replaceFragmentWithoutAnimation(SplashFragment.newInstance(), R.id.splashContainer, false);
    }

    private void check(String[] array) {
        ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList(array));
        HashMap<String, Integer> hashMap = new HashMap<>();
        Collections.sort(stringArrayList);
        String prev = stringArrayList.get(0);
        int currCount = 0;
        for (int i = 0; i < stringArrayList.size(); i++) {
            if (prev.equalsIgnoreCase(stringArrayList.get(i))) {
                currCount++;
                if (hashMap.containsKey(stringArrayList.get(i))) {
                    hashMap.remove(stringArrayList.get(i));
                }
                hashMap.put(stringArrayList.get(i), currCount);
            } else {
                currCount = 1;
                hashMap.put(stringArrayList.get(i), currCount);
                prev = stringArrayList.get(i);
            }
        }
        hashMap = sortByValues(hashMap);
        Object[] keys = hashMap.values().toArray();
        int highestValue = (int) keys[0];
        String highestName = "";
        ArrayList<String> stringArrayList1 = new ArrayList<>();
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            highestName = (String) pair.getKey();
            if (highestValue == (int) pair.getValue()) {
                stringArrayList1.add(highestName);
            }
            it.remove();
        }
        Collections.sort(stringArrayList1);
        Collections.reverse(stringArrayList1);
        StaticUtils.showToast(this, stringArrayList1.get(0));
    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        Collections.reverse(list);
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtRight:
                replaceFragment(LoginFragment.newInstance(), true, R.id.splashContainer);
                break;
            default:
                break;
        }
    }

}
