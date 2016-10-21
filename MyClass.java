package com.example;

import java.awt.MenuBar;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MyClass {

    private static ArrayList<ArrayList<String>> ExpList = new ArrayList<>();

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String getLine;
        do{
            System.out.print("->");
            getLine = sc.nextLine();

            if (getLine.startsWith("!")) {
                if (getLine.startsWith("!simplify")) simplify(getLine);
                else if (getLine.startsWith(("!d/d"))) derivative(getLine);
                else System.out.println("The wrong command");
            } else if (getLine.matches(".*[(|)]*.*]]")) {
                System.out.println("Wrong expression");
            }
            else expression(getLine.trim());

        } while (!getLine.equals("exit"));
        System.out.println("________________");
        System.exit(0);
    }

    // 2 - 4x + x3y + xyz4 - 5z^5

    // !simplify x=1 y=2 z=3

    public static void expression(String Exp){
        ExpList.clear();
        ArrayList<String> TempList = new ArrayList<>();
        String number="";
        int adder = 1;
        boolean IsNegetive = false;
        char[] ExpCharArray = Exp.toCharArray();
        for(int i=0;i<Exp.length();i++){
            char ch = ExpCharArray[i];

            if(Character.isDigit(ch)) {
                number+=ch;
            } else if (Character.isLetter(ch)) {
                String para = "" + ch;
                if(TempList.indexOf(ch+"")==-1){
                    TempList.add(para);
                    TempList.add("1");
                }else{
                    int expUp = Integer.parseInt(TempList.get(TempList.indexOf(ch+"")+1))+1;
                    TempList.set(TempList.indexOf(ch+"")+1, expUp+"");
                }
                if(i!=Exp.length()-1 && ExpCharArray[i+1]=='^'){
                    ++i;++i;
                    para = "";
                    while(i<Exp.length()&&Character.isDigit(ExpCharArray[i])){
                        para+=ExpCharArray[i++];
                    }
                    i--;
                    if(TempList.indexOf(ch+"")==-1){
                        TempList.add(para);
                    }else{
                        int expUp = Integer.parseInt(TempList.get(TempList.indexOf(ch+"")+1))
                                + Integer.parseInt(para)-1;
                        TempList.set(TempList.indexOf(ch+"")+1, expUp+"");
                    }
                }

                if (!number.equals("")) adder*=Integer.parseInt(number);
                number = "";
            }

            if (i==Exp.length()-1||ch=='+'||ch=='-'){

                if(i==0&&ch=='-') {
                    IsNegetive = true;
                    continue;
                }

                if(!number.equals("")) adder*=Integer.parseInt(number);
                if(IsNegetive) TempList.add(adder*-1 + "");
                else TempList.add("+" + adder + "");
                if(!TempList.isEmpty()) ExpList.add(new ArrayList<>(TempList));

                TempList.clear();
                adder=1;
                IsNegetive=ch=='-';//are you OK?
                number = "";
            }
        }

        show(ExpList);
    }

    public static void simplify(String Menbers){
        Scanner sc = new Scanner(Menbers);
        sc.next();
        if(!sc.hasNext()) {
            show(ExpList);
            return;
        }

        ArrayList<ArrayList<String>> SimList  = new ArrayList<>(ExpList);

        while (sc.hasNext()) {
            String[] equation = sc.next().split("=");
            if(equation.length==1) {
                System.out.println("wrong format");
                return;
            }
            String var = equation[0].trim();
            Double value = Double.parseDouble(equation[1]);

            int counter=0;

            for(int i=0;i<SimList.size();i++) {
                ArrayList<String> outList=SimList.get(i);
                if(outList.indexOf(var)!=-1){
                    counter++;
                    Double expUp = Double.parseDouble(outList.get(outList.indexOf(var)+1));
                    expUp = Math.pow(value,expUp) * Double.parseDouble(outList.get(outList.size()-1));

                    if(expUp>0) outList.set(outList.size()-1, "+" + expUp+ "");
                    else outList.set(outList.size()-1, expUp+"");

                    outList.remove(outList.indexOf(var)+1);
                    outList.remove(outList.indexOf(var));
                }

                if(outList.size()==1) continue;
                if(Double.parseDouble(outList.get(outList.size()-1))==0) continue;
                for(int j=0;j<i;j++) {
                    if(SimList.get(j).size()==outList.size()){
                        boolean flag=true;
                        ArrayList<String> mergeList=SimList.get(j);
                        for(int k=0;k<outList.size()-1;k+=2) {
                            if (mergeList.indexOf(outList.get(k)) == -1) {
                                flag = false;
                                break;
                            } else if(!mergeList.get(mergeList.indexOf(outList.get(k))+1).equals(outList.get(k+1))){
                                flag = false;
                                break;
                            }
                        }

                        if(flag){
                            double addUp = Double.parseDouble(mergeList.get(outList.size()-1));
                            addUp+=Double.parseDouble(outList.get(outList.size()-1));
                            if(addUp>0) mergeList.set(outList.size()-1,"+" + addUp);
                            else mergeList.set(outList.size()-1,"" + addUp);
                            outList.set(outList.size()-1,"0");
                        }
                    }
                }
            }
            if(counter==0){
                System.out.println("no var");
                return;
            }
        }

        cleanUp(SimList);

        show(SimList);
    }

    public static void derivative(String Menbers){
        Scanner sc = new Scanner(Menbers);
        sc.next();

        if(!sc.hasNext()){
            System.out.println("wrong format");
            return;
        }

        String der = sc.next();

        if(sc.hasNext()){
            System.out.println("wrong format");
            return;
        }

        if(ExpList.isEmpty()){
            System.out.println("The current expression is empty");
            return;
        }

        ArrayList<ArrayList<String>> DerList  = new ArrayList<>(ExpList);
        //深拷贝不管用 这是BUG 我要报警

        int counter = 0;

        for(int i=0;i<DerList.size();i++) {
            ArrayList<String> outList=DerList.get(i);
            if(outList.indexOf(der)!=-1){
                counter++;
                int expUp = Integer.parseInt(outList.get(outList.indexOf(der)+1));
                if(expUp==1){
                    outList.remove(outList.indexOf(der)+1);
                    outList.remove(outList.indexOf(der));
                }else{
                    outList.set(outList.indexOf(der)+1, expUp-1+"");
                    expUp *= Integer.parseInt(outList.get(outList.size()-1));

                    if(expUp>0) outList.set(outList.size()-1, "+" + expUp+ "");
                    else outList.set(outList.size()-1, expUp+"");
                }
            } else {
                outList.set(outList.size()-1, "0");
                continue;
            }

            if(outList.size()==1) continue;
            if(Double.parseDouble(outList.get(outList.size()-1))==0) continue;

            for(int j=0;j<i;j++) {
                if(DerList.get(j).size()==outList.size()){
                    boolean flag=true;
                    ArrayList<String> mergeList=DerList.get(j);
                    for(int k=0;k<outList.size()-1;k+=2) {
                        if (mergeList.indexOf(outList.get(k)) == -1) {
                            flag = false;
                            break;
                        } else if(!mergeList.get(mergeList.indexOf(outList.get(k))+1).equals(outList.get(k+1))){
                            flag = false;
                            break;
                        }
                    }

                    if(flag){
                        double addUp = Double.parseDouble(mergeList.get(outList.size()-1));
                        addUp+=Double.parseDouble(outList.get(outList.size()-1));
                        if(addUp>0) mergeList.set(outList.size()-1,"+" + addUp);
                        else mergeList.set(outList.size()-1,"" + addUp);
                        outList.set(outList.size()-1,"0");
                    }
                }
            }
        }

        if(counter==0){
            System.out.println("no var");
            return;
        }

        cleanUp(DerList);

        show(DerList);
    }

    // 2 - 4x + x3y + xyz4 - 5z^5

    public static void show(ArrayList<ArrayList<String>> ResultList){
        for(ArrayList<String> outList:ResultList) {
            String partExp = "";
            for(int i=0;i<outList.size()-1;i+=2){
                partExp+=outList.get(i);

                if(!outList.get(i+1).equals("1"))
                    partExp+="^"+outList.get(i+1);
            }

            if(outList.get(outList.size()-1).equals("+1"))
                System.out.print("+"+partExp);
            else if(outList.get(outList.size()-1).equals("-1"))
                System.out.print("-"+partExp);
            else
                System.out.print(outList.get(outList.size()-1)+partExp);
        }
        System.out.println();
    }

    public static void cleanUp(ArrayList<ArrayList<String>> SourceList){
        Iterator<ArrayList<String>> sListIterator = SourceList.iterator();
        double sum = 0;
        while(sListIterator.hasNext()){
            ArrayList<String> remList = sListIterator.next();
            if(Double.parseDouble(remList.get(remList.size()-1))==0){
                sListIterator.remove();
                continue;
            }
            if(remList.size()==1){
                if(Double.parseDouble(remList.get(0))!=0) sum+= Double.parseDouble(remList.get(0));
                sListIterator.remove();
            }
        }

        if(sum!=0||SourceList.isEmpty()){
            ArrayList<String> SumList = new ArrayList<>();
            SumList.add(sum+"");
            SourceList.add(0,SumList);
        }
    }

    public static boolean isNum(String str){
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }
}
// 2 - 4x + x3y + xyz4 - 5z^5