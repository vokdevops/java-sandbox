package org.vokdevops.javasandbox.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vishakh Oommen Koshy on 28/02/2024
 */
public class JsonUtils {

    /**
     * convert json data to Java Map
     * @param jsonData
     * @return
     * @throws IOException
     */
    public static Map<String, Object> jsonDataToMap(String jsonData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * flatten the Maps so each key is a path to the value.
     * For example, the street key in the address object will be flattened to address.street
     * @param map
     * @param prefix
     * @return
     */
    public static Map<String, Object> flatten(Map<String, Object> map, String prefix) {
        Map<String, Object> flatMap = new HashMap<>();
        map.forEach((key, value) -> {
            String newKey = prefix != null ? prefix + "." + key : key;
            if (value instanceof Map) {
                flatMap.putAll(flatten((Map<String, Object>) value, newKey));
            } else if (value instanceof List) {
                // check for list of primitives
                if(((List<?>) value).size() > 0){
                    Object element = ((List<?>) value).get(0);
                    if (element instanceof String || element instanceof Number || element instanceof Boolean) {
                        flatMap.put(newKey, value);
                    } else {
                        // check for list of objects
                        List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                        for (int i = 0; i < list.size(); i++) {
                            flatMap.putAll(flatten(list.get(i), newKey + "[" + i + "]"));
                        }
                    }
                }
            } else {
                flatMap.put(newKey, value);
            }
        });
        return flatMap;
    }

    /**
     * method to convert target bean type to JSON string
     * @param bean
     * @return
     * @throws JsonProcessingException
     */
    public static String serializeJson(Object bean) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(bean);

    }

    /**
     * deserialize JSON string to object identified by the target class
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static <T> T deserializeJson(String jsonString, Class<T> targetClass)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, targetClass);

    }

    /**
     * deserialize JSON string to map
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static Map<String, Object> deserializeJsonToMap(String jsonString) throws JsonParseException, JsonMappingException, IOException{

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> object = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>(){});
        return object;

    }
}
