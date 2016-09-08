package com.ipharmacare.iss.core.testcase;

public class TestOne {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String add = "";
        String a = "123";
        long start = System.currentTimeMillis();
		for (int i = 0 ; i< 10000; i++){
            add  = a + a;
        }
        long end = System.currentTimeMillis();
        System.out.println(end -start);

        start = System.currentTimeMillis();
        for (int i = 0 ; i< 10000; i++){
            StringBuffer buffer = new StringBuffer();
            buffer.append(a);
            buffer.append(a);
            add = buffer.toString();
        }
        end = System.currentTimeMillis();
        System.out.println(end -start);

        start = System.currentTimeMillis();
        for (int i = 0 ; i< 10000; i++){
            add = String.format("%s%s",a,a);
        }
        end = System.currentTimeMillis();
        System.out.println(end -start);
	}

}
