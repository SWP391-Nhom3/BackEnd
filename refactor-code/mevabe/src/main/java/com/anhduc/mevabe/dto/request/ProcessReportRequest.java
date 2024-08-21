package com.anhduc.mevabe.dto.request;

import com.anhduc.mevabe.enums.ReportActionType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessReportRequest {
    UUID staffId;
    ReportActionType actionType;
    String note;
}
