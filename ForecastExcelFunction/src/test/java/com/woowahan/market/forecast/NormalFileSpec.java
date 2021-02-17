package com.woowahan.market.forecast;


public class NormalFileSpec {

//  @Test
//  public void aaa() throws Exception {
//
//    File dir = new File("temp");
//    dir.mkdir();
//    TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(dir));
//
//    ArrayList<ItemRow> itemRows = new ArrayList<>();
//    for (int i = 0; i < 1000; i++) {
//      itemRows.add(
//          ItemRow.builder()
//              .skuCode("sku_code")
//              .skuName("sku_name")
//              .storageMethod("storage_method")
//              .supplierName("supplier_name")
//              .supplierMdName("supplier_md_name")
//              .centerCode("center_code")
//              .centerName("center_name")
//              .data("date_qty")
//              .build()
//      );
//    }
//
//    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
//    SXSSFSheet sheet = workbook.createSheet("sheet1");
//
//    Font font = workbook.createFont();
//    CellStyle cellStyle = workbook.createCellStyle();
//    cellStyle.setFont(font);
//
//    AtomicInteger index = new AtomicInteger(0);
//
//    itemRows.forEach(item -> {
//      SXSSFRow row = sheet.createRow(index.get());
//      SXSSFCell cell0 = row.createCell(0, CellType.STRING);
//      SXSSFCell cell1 = row.createCell(1, CellType.STRING);
//      SXSSFCell cell2 = row.createCell(2, CellType.STRING);
//      SXSSFCell cell3 = row.createCell(3, CellType.STRING);
//      SXSSFCell cell4 = row.createCell(4, CellType.STRING);
//      SXSSFCell cell5 = row.createCell(5, CellType.STRING);
//      SXSSFCell cell6 = row.createCell(6, CellType.STRING);
//      SXSSFCell cell7 = row.createCell(7, CellType.STRING);
//
//      cell0.setCellStyle(cellStyle);
//      cell0.setCellValue(item.getSkuCode());
//
//      cell1.setCellStyle(cellStyle);
//      cell1.setCellValue(item.getSkuName());
//
//      cell2.setCellStyle(cellStyle);
//      cell2.setCellValue(item.getStorageMethod());
//
//      cell3.setCellStyle(cellStyle);
//      cell3.setCellValue(item.getSupplierName());
//
//      cell4.setCellStyle(cellStyle);
//      cell4.setCellValue(item.getSupplierMdName());
//
//      cell5.setCellStyle(cellStyle);
//      cell5.setCellValue(item.getCenterCode());
//
//      cell6.setCellStyle(cellStyle);
//      cell6.setCellValue(item.getCenterName());
//
//      cell7.setCellStyle(cellStyle);
//      cell7.setCellValue(item.getData());
//
//      index.incrementAndGet();
//    });
//
//    File file = new File("test.xlsx");
//    FileOutputStream fos = new FileOutputStream(file);
//    workbook.write(fos);
//    fos.close();
//  }

}
