package com.hornblasters.soundboard2;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hornblasters.core.Address;
import com.hornblasters.core.Checkout;
import com.hornblasters.core.OrdersHelper;
import com.hornblasters.core.ToggledViewPager;

public class CartAddressFragment extends Fragment {
    private static final String TAG = "CartAddressFragment";
    private ToggledViewPager viewPager;
    private Checkout checkout;
    private ViewHolder vh;

    public static Fragment newInstance(Checkout checkout) {
        CartAddressFragment fragment = new CartAddressFragment();
        fragment.checkout = checkout;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        viewPager = (ToggledViewPager) getActivity().findViewById(R.id.pager);
        View v = li.inflate(R.layout.fragment_address, vg, false);
        vh = createViewHolder(v);
        Address address = checkout.address;
        vh.firstName.setText(address.firstName);
        vh.lastName.setText(address.lastName);
        vh.email.setText(address.email);
        vh.phone.setText(address.phone);
        vh.street1.setText(address.street1);
        vh.street2.setText(address.street2);
        vh.city.setText(address.city);
        vh.postal.setText(address.postal);
        vh.territory.setText(address.getTerritory());
        return v;
    }

    private void saveFields() {
        Address address = new Address();
        address._id = 1;

        address.firstName = vh.firstName.getText().toString();
        address.lastName = vh.lastName.getText().toString();
        address.email = vh.email.getText().toString();
        address.phone = vh.phone.getText().toString();
        address.street1 = vh.street1.getText().toString();
        address.street2 = vh.street2.getText().toString();
        address.city = vh.city.getText().toString();
        address.postal = vh.postal.getText().toString();
        address.setTerritory(vh.territory.getText().toString(), "US");

        ((CheckoutActivity)getActivity()).setAddress(address);
        SQLiteDatabase db = new OrdersHelper(getActivity()).getWritableDatabase();
        OrdersHelper.replace(db, address);
    }

    private boolean validateFields(View v) {
        int errors = 0;
        if (vh == null) {
            vh = createViewHolder(v);
        }
        if (vh.firstName.length() == 0) {
            vh.firstName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }
        if (vh.lastName.length() == 0) {
            vh.lastName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }
        if (vh.email.length() == 0) {
            vh.email.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }
        if (vh.phone.length() == 0) {
            vh.phone.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }
        if (vh.street1.length() == 0) {
            vh.street1.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }
        if (vh.city.length() == 0) {
            vh.city.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        }

        if (vh.postal.length() == 0) {
            vh.postal.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.postal.length() < 5) {
            vh.postal.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.postal.length() == 5) {
            vh.postal.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        if (vh.territory.length() == 0) {
            vh.territory.setHintTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.territory.length() < 2) {
            vh.territory.setTextColor(ContextCompat.getColor(getContext(), R.color.hornblasters_red));
            errors++;
        } else if (vh.territory.length() == 2) {
            vh.territory.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        return errors <= 0;

    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder vh = new ViewHolder();
        vh.firstName = (EditText) v.findViewById(R.id.address_first);
        vh.lastName = (EditText) v.findViewById(R.id.address_last);
        vh.email = (EditText) v.findViewById(R.id.address_email);
        vh.phone = (EditText) v.findViewById(R.id.address_phone);
//        vh.company = (EditText) v.findViewById(R.id.address_company);
        vh.street1 = (EditText) v.findViewById(R.id.address_street1);
        vh.street2 = (EditText) v.findViewById(R.id.address_street2);
        vh.city = (EditText) v.findViewById(R.id.address_city);
        vh.postal = (EditText) v.findViewById(R.id.address_postal);
        vh.postal.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        InputFilter numberFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int destStart, int destEnd) {

                if (source instanceof SpannableStringBuilder) {
                    SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder)source;
                    for (int i = end - 1; i >= start; i--) {
                        char currentChar = source.charAt(i);
                        if (!Character.isDigit(currentChar)) {
                            sourceAsSpannableBuilder.delete(i, i+1);
                        }
                    }
                    return source;
                } else {
                    StringBuilder filteredStringBuilder = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        char currentChar = source.charAt(i);
                        if (Character.isDigit(currentChar)) {
                            filteredStringBuilder.append(currentChar);
                        }
                    }
                    return filteredStringBuilder.toString();
                }
            }
        };

        InputFilter letterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int destStart, int destEnd) {

                if (source instanceof SpannableStringBuilder) {
                    SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder)source;
                    for (int i = end - 1; i >= start; i--) {
                        char currentChar = source.charAt(i);
                        if (!Character.isLetter(currentChar)) {
                            sourceAsSpannableBuilder.delete(i, i+1);
                        }
                    }
                    return source;
                } else {
                    StringBuilder filteredStringBuilder = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        char currentChar = source.charAt(i);
                        if (Character.isLetter(currentChar)) {
                            filteredStringBuilder.append(currentChar);
                        }
                    }
                    return filteredStringBuilder.toString();
                }
            }
        };
        vh.postal.setFilters(new InputFilter[] {numberFilter});


        vh.territory = (EditText) v.findViewById(R.id.address_territory);
        vh.territory.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                validateFields(getView());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        vh.territory.setFilters(new InputFilter[]{letterFilter});
        return vh;
    }

    private class ViewHolder {
        public EditText firstName;
        public EditText lastName;
        public EditText email;
        public EditText phone;
        public EditText company;
        public EditText street1;
        public EditText street2;
        public EditText city;
        public EditText postal;
        public EditText territory;
        public EditText country;
    }
}