package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
public class ExcelTool {
    public static void main(String[] args) throws Exception {
        // ====================== 修改为你自己的文件路径 ======================
        String sourcePath = "C:\\testFile\\temp.xlsx";
        String targetPath = "C:\\testFile\\temp2.xlsx";
        // ==================================================================
        // 1. 读取Excel文件，自动识别xls/xlsx格式
        Workbook workbook = WorkbookFactory.create(new FileInputStream(sourcePath));
        Sheet sheet = workbook.getSheetAt(0); // 默认处理第一个工作表，可修改为按名称获取getSheet("表名")
        // 2. 先清除B、C列原有的合并区域，避免新合并区域冲突（如果不需要清空可注释掉这段）
        List<CellRangeAddress> existRegions = sheet.getMergedRegions();
        for (int i = existRegions.size() - 1; i >= 0; i--) {
            CellRangeAddress region = existRegions.get(i);
            // 判断是否是B列(列索引1)或C列(列索引2)的原有合并
            if ((region.getFirstColumn() == 1 && region.getLastColumn() == 1)
                || (region.getFirstColumn() == 2 && region.getLastColumn() == 2)) {
                sheet.removeMergedRegion(i);
            }
        }
        // 3. 创建单元格自动换行样式，保证拼接的多行内容正常显示
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true); // 开启自动换行
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        // 4. 收集A列（列索引0）所有的跨行合并区域，作为B/C列合并的参考
        List<CellRangeAddress> aColMerges = new ArrayList<>();
        for (CellRangeAddress region : sheet.getMergedRegions()) {
            // 筛选出A列的单列合并块（firstColumn和lastColumn都是0，代表单列跨行）
            if (region.getFirstColumn() == 0 && region.getLastColumn() == 0) {
                aColMerges.add(region);
            }
        }
        // 5. 遍历A列的每个合并块，对B、C列做相同范围的合并+内容留存
        for (CellRangeAddress aRegion : aColMerges) {
            int startRow = aRegion.getFirstRow();
            int endRow = aRegion.getLastRow();
            // ---------- 处理B列（列索引1） ----------
            List<String> bColContents = new ArrayList<>();
            // 收集当前合并范围内B列所有行的内容
            for (int rowIdx = startRow; rowIdx <= endRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) row = sheet.createRow(rowIdx);
                Cell cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = getCellValue(cell);
                if (!cellValue.trim().isEmpty()) { // 跳过空内容，避免多余空行
                    bColContents.add(cellValue);
                }
            }
            // 将收集到的所有内容设置到合并区域左上角的单元格（合并后只有左上角内容会显示）
            Row firstRow = sheet.getRow(startRow);
            if (firstRow == null) firstRow = sheet.createRow(startRow);
            Cell bFirstCell = firstRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            bFirstCell.setCellValue(String.join("\n", bColContents)); // 用换行符拼接内容，可改成分号/顿号等分隔符
            bFirstCell.setCellStyle(wrapStyle);
            // 添加B列的合并区域（和A列行范围完全一致，单列跨行）
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 1, 1));
            // ---------- 处理C列（列索引2），逻辑和B列完全一致 ----------
            List<String> cColContents = new ArrayList<>();
            for (int rowIdx = startRow; rowIdx <= endRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) row = sheet.createRow(rowIdx);
                Cell cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = getCellValue(cell);
                if (!cellValue.trim().isEmpty()) {
                    cColContents.add(cellValue);
                }
            }
            Cell cFirstCell = firstRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cFirstCell.setCellValue(String.join("\n", cColContents));
            cFirstCell.setCellStyle(wrapStyle);
            // 添加C列的合并区域
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 2, 2));
            // ==================================================
            // 如果你的需求是【把B、C两列跨列合并成一个大单元格】（不是B/C各自单列合并）
            // 请注释掉上面B列、C列单独addMergedRegion的代码，改用下面这行：
            // 同时需要把B、C列的内容合并到B列第一行单元格即可
            // sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 1, 2));
            // ==================================================
        }
        // 6. 写入处理后的文件
        try (FileOutputStream fos = new FileOutputStream(targetPath)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("处理完成！输出路径：" + targetPath);
    }
    /**
     * 通用方法：读取单元格内容，自动处理字符串、数字、日期、公式等不同类型，避免空指针和格式错误
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        // 如果是公式类型，先取计算后的结果类型
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型按yyyy-MM-dd格式输出，可自行修改格式
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    // 数字类型转字符串，避免科学计数法
                    double numVal = cell.getNumericCellValue();
                    if (numVal == (long) numVal) {
                        return String.valueOf((long) numVal);
                    } else {
                        return String.valueOf(numVal);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
            default:
                return "";
        }
    }
}