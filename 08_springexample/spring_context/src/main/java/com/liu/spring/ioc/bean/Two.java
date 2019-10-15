package com.liu.spring.ioc.bean;

/**
 * spring bean.
 * @Auther: liudongfei
 * @Date: 2019/3/24 22:48
 * @Description:
 */
public class Two {
    private String two;
    private One one;

    /**
     * bean constructor.
     * @param two two
     * @param one one
     */
    public Two(String two, One one) {
        this.one = one;
        this.two = two;
        System.out.println("TWO" + two);
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "Two{"
                + "two='" + two + '\''
                + ", one=" + one
                + '}';
    }
}
