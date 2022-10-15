package com.flightschedule.common.util.json.service;

import com.flightschedule.common.constant.ErrorId;
import com.flightschedule.common.exception.ApplicationServerException;
import com.flightschedule.common.util.Helper;
import com.flightschedule.common.util.json.payload.request.ClassNameRequestDto;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

@Service
public class JsonCreatorServiceImpl implements JsonCreatorService {

    @Override
    public StringBuilder create(ClassNameRequestDto requestDto) {

       try{
           StringBuilder stringBuilder = new StringBuilder("{").append("\n");
           StringBuilder tab = new StringBuilder("\t");
           Class<?> cls = Class.forName(requestDto.getRequestDtoName());
           return extractAttributes(cls, stringBuilder, tab);
       }catch (Exception e){
           String[] split = requestDto.getRequestDtoName().split("\\.");
           throw ApplicationServerException.ErrorJsonCreation(Helper
                   .createDynamicCode(ErrorId.CLASS_NOT_FOUND, split[split.length-1]));
       }

    }

    private <T> StringBuilder extractAttributes(Class<T> className, StringBuilder stringBuilder, StringBuilder tab) {
        Field[] declaredFields = className.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            stringBuilder.append(tab).append("\"").append(declaredField.getName()).append("\"").append(":");
            String[] split = declaredField.getType().getName().split("\\.");

            if (split.length > 1) {

                if (split[2].equals("List") || split[2].equals("Set")) {

                    ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
                    Class<?> type = (Class<?>) genericType.getActualTypeArguments()[0];
                    String typeName = type.getName().split("\\.")[2];
                    tab.append("\t");
                    StringBuilder recursionBuilder = new StringBuilder(" [").append("\n").append(tab);

                    if (checkListClassTypeAndAppendValue(typeName, recursionBuilder, tab)) {

                        tab.deleteCharAt(tab.length() - 1);
                        stringBuilder.append(recursionBuilder).append(tab).append("]").append(",").append("\n");

                    } else {

                        recursionBuilder.append("{").append("\n");
                        if (!type.isEnum()) {
                            StringBuilder extract = extractAttributes(type, recursionBuilder, tab.append("\t"));
                            tab.deleteCharAt(tab.length() - 1);
                            extract.append("\n").append(tab).append("]").append(",").append("\n");
                            stringBuilder.append(extract);
                        } else {
                            stringBuilder.append(" [").append("\n").append(tab).append("Enum or Unknown type")
                                    .append("\n").append(tab).append("]").append(",").append("\n");
                            tab.deleteCharAt(tab.length() - 1);
                        }

                    }
                }

                boolean checked = appendValueWithType(split[2], stringBuilder);

                if (!checked && !split[2].equals("List") && !split[2].equals("Set")) {

                    StringBuilder inside = new StringBuilder(" {").append("\n");
                    Class<?> type = declaredField.getType();
                    if (!type.isEnum()) {

                        StringBuilder extract = extractAttributes(type, inside, tab.append("\t"));
                        extract.append(tab).append(",").append("\n");
                        stringBuilder.append(extract);

                    } else {

                        stringBuilder.append(" \"").append("ENUM or Unknown Type").append("\"").append(",").append("\n");
                    }
                }
            } else {

                appendValueWithType(split[0], stringBuilder);
            }
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        tab.deleteCharAt(tab.length() - 1);
        stringBuilder.append(tab).append("}");
        return stringBuilder;
    }

    private boolean appendValueWithType(String type, StringBuilder stringBuilder) {
        switch (type) {
            case "Long":
                stringBuilder.append(" 1").append(",").append("\n");
                return true;
            case "long":
                stringBuilder.append(" 3").append(",").append("\n");
                return true;
            case "Integer":
                stringBuilder.append(" 2").append(",").append("\n");
                return true;
            case "int":
                stringBuilder.append(" 4").append(",").append("\n");
                return true;
            case "String":
                stringBuilder.append(" \"").append("String").append("\"").append(",").append("\n");
                return true;
            case "Character":
                stringBuilder.append(" \"").append("Character").append("\"").append(",").append("\n");
                return true;
            case "char":
                stringBuilder.append(" \"").append("char").append("\"").append(",").append("\n");
                return true;
            case "Boolean":
                stringBuilder.append(" true").append(",").append("\n");
                return true;
            case "boolean":
                stringBuilder.append(" false").append(",").append("\n");
                return true;
            case "Float":
                stringBuilder.append(" 1.0").append(",").append("\n");
                return true;
            case "float":
                stringBuilder.append(" 3.0").append(",").append("\n");
                return true;
            case "Double":
                stringBuilder.append(" 2.0").append(",").append("\n");
                return true;
            case "double":
                stringBuilder.append(" 4.0").append(",").append("\n");
                return true;
            case "LocalDate":
                stringBuilder.append(" \"").append("2022-01-01").append("\"").append(",").append("\n");
                return true;
            case "LocalTime":
                stringBuilder.append(" \"").append("12:12:12").append("\"").append(",").append("\n");
                return true;
            case "LocalDateTime":
                stringBuilder.append(" \"").append("2022-01-01 12:12:12").append("\"").append(",").append("\n");
                return true;
            default:
                return false;
        }
    }

    private Boolean checkListClassTypeAndAppendValue(String type, StringBuilder stringBuilder, StringBuilder tab) {
        switch (type) {
            case "Long":
                stringBuilder.append(" 1").append(",").append("\n");
                stringBuilder.append(tab).append(" 1").append("\n");
                return true;
            case "Integer":
                stringBuilder.append(" 2").append(",").append("\n");
                stringBuilder.append(tab).append(" 1").append("\n");
                return true;
            case "String":
                stringBuilder.append(" \"").append("String").append("\"").append(",").append("\n");
                stringBuilder.append(tab).append(" \"").append("String").append("\"").append("\n");
                return true;
            case "Character":
                stringBuilder.append(" \"").append("Character").append("\"").append(",").append("\n");
                stringBuilder.append(tab).append(" \"").append("Character").append("\"").append("\n");
                return true;
            case "Boolean":
                stringBuilder.append(" true").append(",").append("\n");
                stringBuilder.append(tab).append(" false").append("\n");
                return true;
            case "Float":
                stringBuilder.append(" 1.0").append(",").append("\n");
                stringBuilder.append(tab).append(" 2.0").append("\n");
                return true;
            case "Double":
                stringBuilder.append(" 2.0").append(",").append("\n");
                stringBuilder.append(tab).append(" 3.0").append("\n");
                return true;
            default:
                return false;
        }
    }

}
