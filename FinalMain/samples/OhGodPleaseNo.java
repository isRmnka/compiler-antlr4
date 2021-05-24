// A test program for TinyJava

class OhGodPleaseNo {
    public static void main(String[] a){
        System.out.println(new Test5().out());
        //new Test5().out();
    }
}

class Test1 {
    int appr() {
        int temp;
        temp = 5;
        return temp;
    }
}

class Test2 {
    int arithmetic() {
        int a;
        int b;
        int res;
        a = 7;
        b = 7;
        res = a*b/a+b-b;

        if (res == 7) {
            System.out.println(res);
        } else {
            res = 0;
        }
        if (res < a+1) {
            System.out.println(res);
        } else {
            res = 0;
        }
        if (res > a-1) {
            System.out.println(res);
        } else {
            res = 0;
        }
        return res;
    }
}

class Test3 {
    boolean logic() {
        boolean temp;
        temp = true && !false || false;
        if (temp == true) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        return temp;
    }
}

class Test4 {
    void wbc() {
        int i;
        i = 0;
        while (i < 10) {
            i = i + 1;
            if (i == 5) {
                continue;
            } else {
                System.out.println(i);
            }
        }
    }
}

class Test5 {
    int out() {
        int temp;
        temp = 777;
        return temp;
    }
}


