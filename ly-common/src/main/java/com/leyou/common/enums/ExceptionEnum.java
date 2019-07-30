package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    BRAND_NOT_FOUND(404,"品牌没查到"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    BRAND_EDIT_ERROR(500,"修改品牌失败"),
    BRAND_DELETE_ERROR(500,"删除品牌失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALTD_FILE_TYPE(400, "无效的文件类型"),
    ;
    private int code;
    private String msg;
}
