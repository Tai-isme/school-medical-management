package com.swp391.school_medical_management.modules.users.dtos.request;

import java.util.List;
import lombok.Data;

@Data
public class NotifyToParentRequest {
   private List<Long> formIds; 
}
