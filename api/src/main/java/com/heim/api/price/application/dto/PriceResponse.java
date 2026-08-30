package com.heim.api.price.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PriceResponse {
    private final BigDecimal basePrice;
    private final BigDecimal originalPrice;
    private final BigDecimal price;
    private final BigDecimal discountedPrice;
    private final String formattedPrice;
    private final double distanceKm;
    private final double timeMin;
    private final String formattedDistance;
    private final String formattedDuration;
    private final List<Map<String, Double>> route;
    private final String addressee;
    private final String recipientPhoneNumber;
    private final boolean isFirstTrip;
    private final int discountPercentage;
    private final BigDecimal discountAmount;
    private final BigDecimal finalPrice;
    private final String formattedFinalPrice;

    public PriceResponse(BigDecimal price, double distanceKm, double timeMin,
                         List<Map<String, Double>> route, String addressee,
                         String recipientPhoneNumber, boolean isFirstTrip,
                         int discountPercentage, BigDecimal discountAmount) {
        this.basePrice = price;
        this.originalPrice = price;
        this.discountAmount = discountAmount;
        this.discountPercentage = discountPercentage;
        this.isFirstTrip = isFirstTrip;
        this.finalPrice = price.subtract(discountAmount);
        this.discountedPrice = isFirstTrip ? this.finalPrice : price;
        this.price = this.discountedPrice;

        this.distanceKm = distanceKm;
        this.timeMin = timeMin;
        this.formattedDistance = String.format("%.1f km", distanceKm);
        this.formattedDuration = String.format("%.0f min", timeMin);
        this.route = route;
        this.addressee = addressee;
        this.recipientPhoneNumber = recipientPhoneNumber;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        this.formattedPrice = nf.format(this.price);
        this.formattedFinalPrice = nf.format(this.finalPrice);
    }

    @JsonProperty("basePrice")
    public BigDecimal getBasePrice() { return basePrice; }

    @JsonProperty("originalPrice")
    public BigDecimal getOriginalPrice() { return originalPrice; }

    @JsonProperty("price")
    public BigDecimal getPrice() { return price; }

    @JsonProperty("discountedPrice")
    public BigDecimal getDiscountedPrice() { return discountedPrice; }

    @JsonProperty("formattedPrice")
    public String getFormattedPrice() { return formattedPrice; }

    @JsonProperty("formattedFinalPrice")
    public String getFormattedFinalPrice() { return formattedFinalPrice; }

    public double getDistanceKm() { return distanceKm; }
    public double getTimeMin() { return timeMin; }
    public String getFormattedDistance() { return formattedDistance; }
    public String getFormattedDuration() { return formattedDuration; }
    public List<Map<String, Double>> getRoute() { return route; }
    public String getAddressee() { return addressee; }
    public String getRecipientPhoneNumber() { return recipientPhoneNumber; }
    public boolean isFirstTrip() { return isFirstTrip; }
    public int getDiscountPercentage() { return discountPercentage; }
    public BigDecimal getDiscountAmount() { return discountAmount; }

    @JsonProperty("finalPrice")
    public BigDecimal getFinalPrice() { return finalPrice; }
}
