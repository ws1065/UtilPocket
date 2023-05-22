package com.sailing.leecode;

import io.swagger.models.auth.In;

import java.util.*;

/**
 * @program: demo
 * @description: 697数值的度
 * 给定一个非空且只包含非负数的整数数组 nums，数组的度的定义是指数组里任一元素出现频数的最大值。
 *
 * 你的任务是在 nums 中找到与 nums 拥有相同大小的度的最短连续子数组，返回其长度。
 * @author: wangsw
 * @create: 2021-02-20 21:17
 */
public class 数组的度 {
    public static void main(String[] args) {

        String a  = null;
        System.out.println();
        ever(a);
        assert a != null;
        System.out.println("nice");
    }

    private static void ever(String a) {

        return ;
    }

    public static int findShortestSubArray(int[] nums){
        //确定数组的度
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.get(nums[i]) == null) {
                map.put(nums[i],1);
            }else {
                Integer times = map.get(nums[i]);
                map.put(nums[i],++times);
            }
        }
        int maxNum=0;
        int maxTimes=0;
        List<Integer> maxNums = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxTimes) {
                maxTimes = entry.getValue();
                maxNum = entry.getKey();
                maxNums.clear();
            }else if (entry.getValue() == maxTimes){
                maxNums.add(entry.getKey());
            }
        }
        if (maxNums.size()!=0) {
            maxNums.add(maxNum);
        }
        //数组的度为maxTimes，
        //匹配度的数字为maxNums或者maxNum

        //获得所有连续子集
        //在连续子集中判断是否含有相同的度
        int result = nums.length;
        for (int i = 0; i < nums.length; i++) {
            int[] newNums = Arrays.copyOf(nums, nums.length - i);


            int[] newNums2 = Arrays.copyOfRange(nums, i, nums.length);


            if (maxNums.size()!=0) {
                for (Integer num : maxNums) {
                    if (Arrays.binarySearch(newNums,num) != -1) {
                        int times =0;
                        for (int newNum : newNums) {
                            if (newNum == num)
                                times++;
                        }
                        if (times == maxTimes)
                            if (newNums.length < result){
                                result = newNums.length;
                            }
                    }
                    if (Arrays.binarySearch(newNums2,num) != -1) {
                        int times =0;
                        for (int newNum : newNums2) {
                            if (newNum == num)
                                times++;
                        }
                        if (times == maxTimes)
                            if (newNums2.length < result){
                                result = newNums2.length;
                            }
                    }
                }
            }else {

                if (Arrays.binarySearch(newNums,maxNum) !=-1) {
                    int times =0;
                    for (int newNum : newNums) {
                        if (newNum == maxNum)
                            times++;
                    }
                    if (times == maxTimes)
                        if (newNums.length < result){
                            result = newNums.length;
                        }
                }
                if (Arrays.binarySearch(newNums2,maxNum) !=-1) {
                    int times =0;
                    for (int newNum : newNums2) {
                        if (newNum == maxNum)
                            times++;
                    }
                    if (times == maxTimes)
                        if (newNums2.length < result){
                            result = newNums2.length;
                        }
                }
            }

        }
        return result;
    }
}