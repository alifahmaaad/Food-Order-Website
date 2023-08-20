package com.restoreserve.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData<Obj>{
    private boolean status;
    private List<String> message=new ArrayList<>();
    private Obj payload;
}
