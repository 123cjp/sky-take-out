package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.MacSpi;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 陈建平
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        /*
         日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
         private String dateList;
         */
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (!begin.equals(end)) {
            //计算日期后一天直到最后一天
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //StringUtils.join(dataList, ",");
        //存放营业额的
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dataList) {
            //查询data日期对应的营业额数据--营业额是值状态为已完成的订单金额
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//<----这一天的开始时间0：00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//<----这一天最后时间23：59：59...
            //select sum(amount) from orders where order_time > ? and order_time  < ? and status = 5
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            //如果营业额查出来为0，会显示为null,转成0.0就可以了
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);


        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dataList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (!begin.equals(end)) {
            //计算日期后一天直到最后一天
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存放每天新增用户数量---> select count(id) from user where create_time < ? and create_time > ?
        List<Integer> newUserList = new ArrayList<>();
        //存放每天中用户--->select count(id) from user where create_time < ?
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate localDate : dataList) {
            //查询data日期对应的营业额数据--营业额是值状态为已完成的订单金额
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);//<----这一天的开始时间0：00
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//<----这一天最后时间23：59：59...

            Map map = new HashMap();
            map.put("endTime", endTime);
            //总用户数量
            Integer totalUser = userMapper.countByMap(map);
            map.put("beginTime", beginTime);
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间的每天对应的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (!begin.equals(end)) {
            //计算日期后一天直到最后一天
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存放每天订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();
        //遍历dataList集合，查询每天的有效订单数和订单
        for (LocalDate localDate : dataList) {
            //查询data日期对应的营业额数据--营业额是值状态为已完成的订单金额
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);//<----这一天的开始时间0：00
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//<----这一天最后时间23：59：59...

            //每天订单总数
            //select count(id) from order where order_time > ? and order_time < ?;
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //每天有效订单数
            //select count(id) from order where order_time > ? and order_time < ? and status = 5;
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间内订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间内有效订单总数
        Integer totalValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }


    /**
     * 根据条件条件订单数量
     *
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 指定时间内区间销量排名前10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//<----这一天的开始时间0：00
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);//<----这一天最后时间23：59：59...

        //select od.name,sum(od.number)
        //from order_detail od,orders o
        //where od.order.id = o.id and o.status = 5 and o.order_time >? and o.order_time < ?
        //group by od.name
        //order by od.number desc
        //limit 0,10

//        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
//        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}