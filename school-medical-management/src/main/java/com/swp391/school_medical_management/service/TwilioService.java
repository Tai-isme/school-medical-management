package com.swp391.school_medical_management.service;

import com.swp391.school_medical_management.config.TwilioConfig;
import com.swp391.school_medical_management.modules.users.services.impl.UserService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Autowired
    private TwilioConfig twilioConfig;

    public TwilioService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public String sendOtp(String to, String otp) {
        logger.info("Sending OTP to " + to);
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
                otp
        ).create();
        return message.getSid();
    }

    public String formatPhoneToInternational(String phone) {
        if (phone.startsWith("0")) {
            return "+84" + phone.substring(1);
        }
        return phone; // Nếu đã đúng định dạng quốc tế
    }

}
