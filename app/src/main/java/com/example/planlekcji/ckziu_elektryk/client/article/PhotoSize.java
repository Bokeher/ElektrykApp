package com.example.planlekcji.ckziu_elektryk.client.article;

public enum PhotoSize {
    SIZE_255x300("255x300"),
    SIZE_150x150("150x150"),
    SIZE_768x700("768x700"),
    SIZE_768x430("768x430"),
    SIZE_100x75("100x75"),
    SIZE_580x460("580x460"),
    SIZE_350x220("350x220"),
    SIZE_150x120("150x120"),
    SIZE_390x390("390x390"),
    SIZE_262x262("262x262"),
    SIZE_470x560("470x560"),
    SIZE_370x240("370x240"),
    SIZE_768x500("768x500"),
    SIZE_345x230("345x230"),
    SIZE_380x220("380x220"),
    SIZE_160x110("160x110"),
    SIZE_300x400("300x400"),
    SIZE_340x453("340x453"),
    SIZE_768x550("768x550"),
    SIZE_278x185("278x185"),
    SIZE_350x100("350x100"),
    SIZE_FULL("full");

    private final String name;

    PhotoSize(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
