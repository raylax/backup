package com.jiaomatech.blackbook.common.process;

import java.util.List;
import java.util.Map;

@AutoBean
public interface AutoBeanTest {

    List<String> hello(String s1, Map<String, Integer> map);

}
