package com.gls.parcelshop.controller;

import com.gls.parcelshop.model.DeliveryState;
import com.gls.parcelshop.model.Parcel;
import com.gls.parcelshop.repository.ParcelRepository;
import com.gls.parcelshop.service.NotificationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ParcelController {

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/parcels")
    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll();
    }

    @PostMapping(value = "/parcels", consumes = "application/json")
    public ResponseEntity<Parcel> insertNewParcels(@RequestBody Parcel parcel) {
        Parcel savedParcel = parcelRepository.save(parcel);
        notificationService.notifySomeoneAboutChange(savedParcel);
        return new ResponseEntity<>(savedParcel, HttpStatus.CREATED);
    }

    @GetMapping("/parcels/out-for-delivery")
    public List<Parcel> getParcelsOutForDelivery(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") Date date) {

        if (date != null) {
            // If only one date is provided, find parcels for this date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String dateString = formatter.format(date);
            return parcelRepository.findByDeliveryDateAndDeliveryState(dateString, DeliveryState.OUT_FOR_DELIVERY);
        } else {
            // If no dates are provided, find all parcels out for delivery
            return parcelRepository.findByDeliveryState(DeliveryState.OUT_FOR_DELIVERY);
        }
    }

}
