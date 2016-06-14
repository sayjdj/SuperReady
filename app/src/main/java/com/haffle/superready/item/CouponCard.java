package com.haffle.superready.item;

public class CouponCard {

    String text;
    String button;

    public CouponCard(String text, String button) {
        this.text = text;
        this.button = button;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }
}
