package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hornblasters.core.Address;
import com.hornblasters.core.Checkout;
import com.hornblasters.core.Payment;
import com.hornblasters.core.ToggledViewPager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutPurchaseFragment extends Fragment {
    private static final String TAG = "CheckoutPaymentFragment";
    private ToggledViewPager viewPager;
    private boolean inProcess = false;
    private final OkHttpClient client = new OkHttpClient();

    public static Fragment newInstance() {
        return new CheckoutPurchaseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        viewPager = (ToggledViewPager) getActivity().findViewById(R.id.pager);
        View v = li.inflate(R.layout.fragment_purchase, vg, false);
        TextView subtotal = (TextView) v.findViewById(R.id.price_subtotal);
        subtotal.setText(NumberFormat.getCurrencyInstance(Locale.US).format(((CheckoutActivity)getActivity()).getSubtotal()));
        TextView total = (TextView) v.findViewById(R.id.price_total);
        total.setText(NumberFormat.getCurrencyInstance(Locale.US).format(((CheckoutActivity)getActivity()).getSubtotal()));
        Button button = (Button) v.findViewById(R.id.back_button);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

        Button purchaseButton = (Button) v.findViewById(R.id.purchase);
        purchaseButton.setEnabled(true);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPurchase();
            }
        });
        return v;
    }

    public void attemptPurchase() {
        if (inProcess) {
            return;
        }
        try {
            CheckoutActivity activity = (CheckoutActivity) getActivity();
            Checkout checkout = activity.getCheckout();
            Address address = checkout.address;
            Payment payment = checkout.payment;

            if (!checkout.ready()) {
                // Handle errors...
                return;
            }

            RequestBody formBody = new FormEncodingBuilder()
                    .add("ccname", address.firstName + " " + address.lastName)
                    .add("cccompany", address.company)
                    .add("ccaddress1", address.street1)
                    .add("ccaddress2", address.street2)
                    .add("cccity", address.city)
                    .add("ccprovince", address.getTerritory())
                    .add("ccpostal", address.postal)
                    .add("cccountry", address.getCountry())
                    .add("ccphone", address.phone)
                    .add("ccemail", address.email)
                    .add("ccnum", payment.cardNumber)
                    .add("ccmon", payment.expiryMonth)
                    .add("ccyr", payment.expiryYear)
                    .add("cccvv", payment.cvv2)
                    .add("total", Double.toString(activity.getTotal()))
                    .add("shipping", "UPS_GND")
                    .build();


            Request request = new Request.Builder()
                    .url("https://us-east1.hornblasters.com/api/checkout.php")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);
            boolean approved = new CheckoutPurchaseParser().parse(response.body().string());

            if (approved) {
                Log.d(TAG, "approved");
            } else {
                Log.d(TAG, "declined");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}