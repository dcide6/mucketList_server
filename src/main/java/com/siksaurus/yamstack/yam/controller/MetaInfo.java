package com.siksaurus.yamstack.yam.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MetaInfo {

    private Map<String, Set<String>> regionInfo;
    private Set<String> categories;
    private Set<String> tags;
    private int yamSize;
    private int completeSize;
    private int noRevisitSize;
}
