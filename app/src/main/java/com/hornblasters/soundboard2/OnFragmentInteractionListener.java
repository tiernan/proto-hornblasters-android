package com.hornblasters.soundboard2;

public interface OnFragmentInteractionListener {
    enum Action {CATEGORY, PRODUCT}
    void onFragmentInteraction(Action action, int id);
}