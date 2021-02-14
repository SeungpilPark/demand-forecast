package com.woowahan.market.forecast

import com.mz.poi.mapper.ExcelMapper
import com.woowahan.market.forecast.order.FirstInfoRow
import com.woowahan.market.forecast.order.ItemRow
import com.woowahan.market.forecast.order.OrderExcel
import com.woowahan.market.forecast.order.OrderSheet
import com.woowahan.market.forecast.order.SecondInfoRow
import com.woowahan.market.forecast.order.SummaryRow
import com.woowahan.market.forecast.order.TitleRow
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

class OrderExcelSpec extends Specification {

    def createExcelDto() {
        def model = OrderExcel.builder()
                .sheet(
                        OrderSheet.builder()
                                .titleRow(TitleRow.builder()
                                        .title("마감 발주서(발주번호)")
                                        .build())
                                .firstInfos(
                                        Stream.of(
                                                FirstInfoRow.builder()
                                                        .supplierText("거래처명")
                                                        .supplierValue("A")
                                                        .ordererText("발주처명")
                                                        .ordererValue("B")
                                                        .build(),
                                                FirstInfoRow.builder()
                                                        .supplierText("담당자")
                                                        .supplierValue("A")
                                                        .ordererText("담당MD")
                                                        .ordererValue("B")
                                                        .build(),
                                                FirstInfoRow.builder()
                                                        .supplierText("담당자 이메일")
                                                        .supplierValue("A")
                                                        .ordererText("담당 MD 이메일")
                                                        .ordererValue("B")
                                                        .build(),
                                                FirstInfoRow.builder()
                                                        .supplierText("담당자 연락처")
                                                        .supplierValue("A")
                                                        .ordererText("담당 MD 연락처")
                                                        .ordererValue("B")
                                                        .build()
                                        ).collect(Collectors.toList())
                                )
                                .secondInfos(
                                        Stream.of(
                                                SecondInfoRow.builder()
                                                        .supplierText("발주 번호")
                                                        .supplierValue("A")
                                                        .ordererText("입고지 명 (입고지 주소)")
                                                        .ordererValue("B")
                                                        .build(),
                                                SecondInfoRow.builder()
                                                        .supplierText("발주 등록일")
                                                        .supplierValue("2021-01-26")
                                                        .ordererText("입고지 연락처 (층)")
                                                        .ordererValue("B")
                                                        .build(),
                                                SecondInfoRow.builder()
                                                        .supplierText("입고 예정일")
                                                        .supplierValue("2021-01-26")
                                                        .ordererText("보관 온도")
                                                        .ordererValue("C")
                                                        .build()
                                        ).collect(Collectors.toList())
                                )
                                .summaryRow(
                                        SummaryRow.builder()
                                                .summaryTitle("총 발주 금액")
                                                .build()
                                )
                                .items(
                                        Stream.of(
                                                ItemRow.builder()
                                                        .number(1)
                                                        .skuName("A")
                                                        .skuBarcode("A")
                                                        .skuCode("")
                                                        .skuSupplierItemCode("")
                                                        .skuPrice(BigDecimal.valueOf(1200))
                                                        .orderedQuantity(30)
                                                        .skuQuantityOfBox(10)
                                                        .receivedBoxQuantity(3)
                                                        .skuExpirationGuide(LocalDateTime.now())
                                                        .skuManufacturedGuide(LocalDateTime.now())
                                                        .build(),
                                                ItemRow.builder()
                                                        .number(2)
                                                        .skuName("A")
                                                        .skuBarcode("A")
                                                        .skuCode("")
                                                        .skuSupplierItemCode("")
                                                        .skuPrice(BigDecimal.valueOf(2100))
                                                        .orderedQuantity(40)
                                                        .skuQuantityOfBox(10)
                                                        .receivedBoxQuantity(4)
                                                        .skuExpirationGuide(LocalDateTime.now())
                                                        .skuManufacturedGuide(LocalDateTime.now())
                                                        .build()
                                        ).collect(Collectors.toList())
                                )
                                .build()
                )
                .build()
        return model
    }

    def "입고후 발주서 모델이 엑셀로 변환된다"() {
        given:
        def model = createExcelDto()

        when:
        def excel = ExcelMapper.toExcel(model)
        File file = new File("after_closed_order.xlsx")
        FileOutputStream fos = new FileOutputStream(file)
        excel.write(fos)
        fos.close()

        then:
        1 == 1
    }

    def "입고후 발주서 엑셀이 모델로 변환된다"() {
        given:
        def excelDto = createExcelDto()

        when:
        def excel = ExcelMapper.toExcel(excelDto)
        def dto = ExcelMapper.fromExcel(excel, OrderExcel.class)

        then:
        dto.getSheet().getItems().size() == 2
    }

    def "입고전 발주서 엑셀 모델이 엑셀로 변환된다"() {
        given:
        def model = createExcelDto()
        model.getSheet().getItems().forEach({ item ->
            item.setTotalAmountFormula("product({{this.skuPrice}},{{this.orderedQuantity}})")
        })
        def beforeClosedExcelStructure = OrderExcel.beforeClosedExcelStructure

        when:
        def excel = ExcelMapper.toExcel(model, beforeClosedExcelStructure)
        File file = new File("before_closed_order.xlsx")
        FileOutputStream fos = new FileOutputStream(file)
        excel.write(fos)
        fos.close()

        then:
        1 == 1
    }

    def "입고전 발주서 엑셀이 모델로 변환된다"() {
        given:
        def model = createExcelDto()
        model.getSheet().getItems().forEach({ item ->
            item.setTotalAmountFormula("product({{this.skuPrice}},{{this.orderedQuantity}})")
        })
        def beforeClosedExcelStructure = OrderExcel.beforeClosedExcelStructure

        when:
        def excel = ExcelMapper.toExcel(model, beforeClosedExcelStructure)
        def dto = ExcelMapper.fromExcel(excel, OrderExcel.class, beforeClosedExcelStructure)

        then:
        dto.getSheet().getItems().size() == 2
    }
}
