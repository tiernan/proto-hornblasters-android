package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hornblasters.core.Payment;
import com.hornblasters.core.ToggledViewPager;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutPaymentFragment extends Fragment {
    private static final String TAG = "CheckoutPaymentFragment";
    private ViewHolder vh;

    private ToggledViewPager viewPager;
    private Payment payment;

    public static Fragment newInstance(Payment payment) {
        CheckoutPaymentFragment fragment = new CheckoutPaymentFragment();
        fragment.setRetainInstance(true);
        fragment.payment = payment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        viewPager = (ToggledViewPager) getActivity().findViewById(R.id.pager);
        View v = li.inflate(R.layout.fragment_payment, vg, false);
        TextView subtotal = (TextView) v.findViewById(R.id.price_subtotal);
        subtotal.setText(NumberFormat.getCurrencyInstance(Locale.US).format(((CheckoutActivity)getActivity()).getSubtotal()));
        TextView total = (TextView) v.findViewById(R.id.price_total);
        total.setText(NumberFormat.getCurrencyInstance(Locale.US).format(((CheckoutActivity)getActivity()).getSubtotal()));
        Button backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setEnabled(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        Button button = (Button) v.findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(v.getRootView())) {
                    saveFields();
                    viewPager.setCurrentItem(3);
                }
            }
        });
        return v;
    }

    private void saveFields() {
        ((CheckoutActivity)getActivity()).setPayment(
                vh.number.getText().toString(),
                vh.expiryMonth.getText().toString(),
                vh.expiryYear.getText().toString(),
                vh.cvv2.getText().toString()
        );
    }

    private boolean validateFields(View v) {
        int errors = 0;
        if (vh == null) {
            vh = createViewHolder(v);
        }

        if (vh.number.length() == 0) {
            vh.number.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.number.length() < 15) {
            vh.number.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.number.length() >= 15) {
            vh.number.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        if (vh.expiryMonth.length() == 0) {
            vh.expiryMonth.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.expiryMonth.length() < 2) {
            vh.expiryMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.expiryMonth.length() == 2) {
            vh.expiryMonth.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        if (vh.expiryYear.length() == 0) {
            vh.expiryYear.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.expiryYear.length() < 2) {
            vh.expiryYear.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.expiryYear.length() == 2) {
            vh.expiryYear.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        if (vh.cvv2.length() == 0) {
            vh.cvv2.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.cvv2.length() < 3) {
            vh.cvv2.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.cvv2.length() >= 3) {
            vh.cvv2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        return errors <= 0;

    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.number = (EditText) v.findViewById(R.id.payment_number);
        vh.expiryMonth = (EditText) v.findViewById(R.id.payment_expiry_month);
        vh.expiryYear = (EditText) v.findViewById(R.id.payment_expiry_year);
        vh.cvv2 = (EditText) v.findViewById(R.id.payment_cvv2);


        vh.number.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        vh.expiryMonth.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        vh.expiryYear.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        vh.cvv2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return vh;
    }

    private class ViewHolder {
        public EditText number;
        public EditText expiryMonth;
        public EditText expiryYear;
        public EditText cvv2;
    }
}