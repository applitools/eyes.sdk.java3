package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.visualgrid.model.NMGOptions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NMGOptionsMapper {

    public static Map<String, Object> toNMGOptionsMapperDtoList(List<NMGOptions> nmgOptions) {
        if (nmgOptions == null || nmgOptions.isEmpty()) {
            return null;
        }

        Map<String, Object> ufgOptionsMap = nmgOptions.stream()
                .filter(nmgOption -> nmgOption.getKey().equals("nonNMGCheck") && nmgOption.getKey() != null
                                    && nmgOption.getValue().equals("addToAllDevices") && nmgOption.getValue() != null)
                .collect(Collectors.toMap(NMGOptions::getKey, NMGOptions::getValue));

        return ufgOptionsMap.size() == 0? null : ufgOptionsMap;
    }

}
