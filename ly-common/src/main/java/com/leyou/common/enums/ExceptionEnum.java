package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    CATEGORY_ADD_EOORO(500, "商品分类添加失败!"),
    CATEGORY_DELETE_EORROR(500, "商品分类删除失败!"),
    CATEGORY_EDIT_ERROR(500, "商品分类修改失败!"),
    BRAND_NOT_FOUND(404,"品牌没查到"),
    SPUDETAIL_NOT_FOUND(404, "spu具体信息没查到"),
    SKU_NOT_FOUND(404, "sku没查到"),
    STOCK_NOT_FOUND(404, "STOCK没找到"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    BRAND_EDIT_ERROR(500,"修改品牌失败"),
    SALEABLE_CHANGE_ERROR(500, "修改上下架失败"),
    BRAND_DELETE_ERROR(500,"删除品牌失败"),
    GOOD_DELETE_ERROR(500, "删除商品失败"),
    GOOD_EDIT_ERROR(500, "修改商品失败"),
    GOOD_SAVE_ERROR(500, "商品保存失败"),
    SPECIFICATION_SAVE_ERROR(500, "保存规格失败"),
    SPECIFICATION_ADD_EEEOR(500, "规格添加失败"),
    SPECIFICATION_EDIT_EEEOR(500, "规格修改失败"),
    SPECIFICATION_DELETE_EEEOR(500, "规格删除失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALTD_FILE_TYPE(400, "无效的文件类型"),
    ;
    private int code;
    private String msg;
}
