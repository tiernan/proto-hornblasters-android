package com.hornblasters.soundboard2;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hornblasters.core.Address;
import com.hornblasters.core.Cart;
import com.hornblasters.core.ToggledViewPager;
import com.hornblasters.xml.Product;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckoutShippingFragment extends Fragment {
    private static final String TAG = "CheckoutShippingFragment";

    private ToggledViewPager viewPager;
    private Address address;
    private Cart cart;
    private final OkHttpClient client = new OkHttpClient();

    public static Fragment newInstance(Cart cart, Address address) {
        CheckoutShippingFragment fragment = new CheckoutShippingFragment();
        fragment.setRetainInstance(true);
        fragment.cart = cart;
        fragment.address = address;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        viewPager = (ToggledViewPager) getActivity().findViewById(R.id.pager);

        View v = li.inflate(R.layout.fragment_shipping, vg, false);
        Button backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setEnabled(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        new PostXml().execute("");
        return v;
    }


    private void saveFields() {
        // REMOVED FOR GITHUB
    }

    private String generateXmlBody() throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        String ns = "";
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag(ns, "request");
        serializer.startTag(ns, "shipping");

        serializer.startTag(ns, "address");

        serializer.startTag(ns, "postal");
        serializer.text(address.postal);
        serializer.endTag(ns, "postal");

        serializer.startTag(ns, "territory");
        serializer.text(address.getTerritory());
        serializer.endTag(ns, "territory");

        serializer.startTag(ns, "country");
        serializer.text(address.getCountry());
        serializer.endTag(ns, "country");

        serializer.endTag(ns, "address");

        serializer.startTag(ns, "cart");

        for (Product product : cart.products) {
            serializer.startTag(ns, "part");

            serializer.startTag(ns, "number");
            serializer.text(product.number);
            serializer.endTag(ns, "number");
            serializer.startTag(ns, "quantity");
            serializer.text(Integer.toString(product.quantity));
            serializer.endTag(ns, "quantity");

            serializer.endTag(ns, "part");
        }

        serializer.endTag(ns, "cart");

        serializer.endTag(ns, "shipping");
        serializer.endTag(ns, "request");
        serializer.endDocument();

        return writer.toString();
    }

    private class PostXml extends AsyncTask<String, Void, ArrayList<ShippingMethod>> {
        private static final String TAG = "DownloadXmlTaskNative";

        @Override
        protected ArrayList<ShippingMethod> doInBackground(String... urls) {
            try {
                Request request = new Request.Builder()
                        .url("https://us-east1.hornblasters.com/api/shipping.php")
                        .post(RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),
                                generateXmlBody()))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
                return new CheckoutShippingParser().parse(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ShippingMethod> result) {
            if (result != null) {

                Activity activity = getActivity();
                View progressBar = activity.findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);

                RadioGroup shippingMethods = (RadioGroup) activity.findViewById(R.id.cart_shipping_methods);

                int[][] states = new int[][] {
                        new int[] {-android.R.attr.state_checked},
                        new int[] {android.R.attr.state_checked},
                };

                int[] colors = new int[] {
                        Color.RED,
                        Color.BLACK,
                };

                ColorStateList colorList = new ColorStateList(states, colors);

                Log.d(TAG, "MethodCount: " + result.size());

                for (ShippingMethod method : result) {
                    Log.d(TAG, "Trying: " + method.type);
                    String methodName = null;

                    switch (method.type) {
                        case "UPS_GND":
                            methodName = activity.getString(R.string.shipping_method_ups_ground);
                            break;
                        case "UPS_3DS":
                            methodName = activity.getString(R.string.shipping_method_ups_3day);
                            break;
                        case "UPS_2DA":
                            methodName = activity.getString(R.string.shipping_method_ups_2day);
                            break;
                        case "UPS_1DA":
                            methodName = activity.getString(R.string.shipping_method_ups_1day);
                            break;
                        default:
                            Log.d(TAG, "Could not find: " + method.type);
                    }

                    if (methodName != null) {
                        RadioButton view = new RadioButton(activity);
                        view.setText(String.format(getString(R.string.shipping_method_with_price), methodName, NumberFormat.getCurrencyInstance(Locale.US).format(method.cost)));
                        view.setTextColor(Color.BLACK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.setButtonTintList(colorList);
                        }
                        shippingMethods.addView(view);
                    }
                }

                Button button = (Button) activity.findViewById(R.id.payment);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                viewPager.setCurrentItem(3);

                    }
                });

            } else {
                Log.d(TAG, "ERROR!");
            }
        }
    }
}