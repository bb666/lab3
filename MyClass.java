package com.example;

import java.awt.MenuBar;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MyClass {

  private static ArrayList<ArrayList<String>> ExpList = new ArrayList<>();

  /**.
   * @param args []
  */
  public static void main(String[] args) { 
    Scanner sc = new Scanner(System.in);
    String getLine;
    do { 
      System.out.print("->");
      getLine = sc.nextLine();

      if (getLine.startsWith("!")) {
        if (getLine.startsWith("!simplify")) { 
          simplify(getLine); 
        } else if (getLine.startsWith(("!d/d"))) { 
          derivative(getLine); 
        } else { 
          System.out.println("The wrong command");
        }
      } else if (getLine.matches(".*[(|)]*.*]]")) {
        System.out.println("Wrong expression");
      } else { 
        expression(getLine.trim());
      }

    } while (!getLine.equals("exit"));
    System.out.println("________________");
    System.exit(0);
  }

  // 2 - 4x + x3y + xyz4 - 5z^5

  // !simplify x=1 y=2 z=3

  /**.
  * @param exp string
  */
  public static void expression(String exp) { 
    ExpList.clear();
    ArrayList<String> tempList = new ArrayList<>();
    String number = "";
    int adder = 1;
    boolean isNegetive = false;
    char[] expCharArray = exp.toCharArray();
    for (int i = 0;i < exp.length();i++) { 
      char ch = expCharArray[i];

      if (Character.isDigit(ch)) {
        number += ch;
      } else if (Character.isLetter(ch)) {
        String para = "" + ch;
        if (tempList.indexOf(ch + "") == -1) { 
          tempList.add(para);
          tempList.add("1");
        } else { 
          int expUp = Integer.parseInt(tempList.get(tempList.indexOf(ch + "") + 1)) + 1;
          tempList.set(tempList.indexOf(ch + "") + 1, expUp + "");
        }
        if (i != exp.length() - 1 && expCharArray[i + 1] == '^') { 
          ++i;
          ++i;
          para = "";
          while (i < exp.length() && Character.isDigit(expCharArray[i])) { 
            para += expCharArray[i++];
          }
          i--;
          if (tempList.indexOf(ch + "") == -1) { 
            tempList.add(para);
          } else { 
            int expUp = Integer.parseInt(tempList.get(tempList.indexOf(ch + "") + 1))
                                + Integer.parseInt(para) - 1;
            tempList.set(tempList.indexOf(ch + "") + 1, expUp + "");
          }
        }

        if (!number.equals("")) { 
          adder *= Integer.parseInt(number); 
        } 
        number = "";
      }

      if (i == exp.length() - 1 || ch == '+' || ch == '-') { 

        if (i == 0 && ch == '-') {
          isNegetive = true;
          continue;
        }

        if (!number.equals("")) { 
          adder *= Integer.parseInt(number);
        }
        if (isNegetive) { 
          tempList.add(adder * - 1 + "");
        } else { 
          tempList.add("+" + adder + ""); 
        }
        if (!tempList.isEmpty()) { 
          ExpList.add(new ArrayList<>(tempList));
        }

        tempList.clear();
        adder = 1;
        isNegetive = ch == '-';//are you OK?
        number = "";
      }
    }

    show(ExpList);
  }

  /**.
  * @param menbers string
  */
  public static void simplify(String menbers) { 
    Scanner sc = new Scanner(menbers);
    sc.next();
    if (!sc.hasNext()) { 
      show(ExpList);
      return;
    }

    ArrayList<ArrayList<String>> simList  = new ArrayList<>(ExpList);

    while (sc.hasNext()) {
      String[] equation = sc.next().split("=");
      if (equation.length == 1) { 
        System.out.println("wrong format");
        return;
      }
      String var = equation[0].trim();
      Double value = Double.parseDouble(equation[1]);

      int counter = 0;

      for (int i = 0;i < simList.size();i++) { 
        ArrayList<String> outList = simList.get(i);
        if (outList.indexOf(var) != -1) {  
          counter++;
          Double expUp = Double.parseDouble(outList.get(outList.indexOf(var) + 1));
                    expUp = Math.pow(value,expUp) * Double.parseDouble(outList.get(outList.size() - 1));

          if (expUp > 0) { 
            outList.set(outList.size() - 1, "+" + expUp + "");
          } else { 
            outList.set(outList.size() - 1, expUp + ""); 
          } 
          outList.remove(outList.indexOf(var) + 1);
          outList.remove(outList.indexOf(var));
        }

        if (outList.size() == 1) { 
          continue;
        }
        if (Double.parseDouble(outList.get(outList.size() - 1)) == 0) { 
          continue;
        }
        for (int j = 0;j < i;j++) {
          if (simList.get(j).size() == outList.size()) { 
            boolean flag = true;
            ArrayList<String> mergeList = simList.get(j);
            for (int k = 0;k < outList.size() - 1;k += 2) { 
              if (mergeList.indexOf(outList.get(k)) == -1) {
                flag = false;
                break;
              } else if(!mergeList.get(mergeList.indexOf(outList.get(k)) + 1).equals(outList.get(k + 1))){
                flag = false;
                break;
              }
            }

            if (flag) { 
              double addUp = Double.parseDouble(mergeList.get(outList.size() - 1));
              addUp += Double.parseDouble(outList.get(outList.size() - 1));
              if (addUp > 0) { 
                mergeList.set(outList.size() - 1,"+" + addUp);
              } else { 
                mergeList.set(outList.size() - 1,"" + addUp);
              }
              outList.set(outList.size() - 1,"0");
            }
          }
        }
      }
      if (counter == 0) { 
        System.out.println("no var");
        return;
      }
    }

    cleanUp(simList);

    show(simList);
  }

  /**.
  * @param menbers string
  */
  public static void derivative(String menbers) { 
    Scanner sc = new Scanner(menbers);
    sc.next();

    if (!sc.hasNext()) { 
      System.out.println("wrong format");
      return;
    }

    String der = sc.next();

    if (sc.hasNext()) { 
      System.out.println("wrong format");
      return;
    }

    if (ExpList.isEmpty()) { 
      System.out.println("The current expression is empty");
      return;
    }

    ArrayList<ArrayList<String>> derList  = new ArrayList<>(ExpList);
    //深拷贝不管用 这是BUG 我要报警

    int counter = 0;

    for (int i = 0;i < derList.size();i++) {
      ArrayList<String> outList = derList.get(i);
      if (outList.indexOf(der) != -1){
        counter++;
        int expUp = Integer.parseInt(outList.get(outList.indexOf(der) + 1));
        if (expUp == 1){
          outList.remove(outList.indexOf(der) + 1);
          outList.remove(outList.indexOf(der));
        } else {
          outList.set(outList.indexOf(der) + 1, expUp - 1 + "");
          expUp *= Integer.parseInt(outList.get(outList.size() - 1));

          if (expUp > 0) { 
            outList.set(outList.size() - 1, "+" + expUp + "");
          } else { 
            outList.set(outList.size() - 1, expUp + "");
          }
        }
      } else { 
        outList.set(outList.size() - 1, "0");
        continue;
      }

      if (outList.size() == 1) { 
        continue;
      }
      if (Double.parseDouble(outList.get(outList.size() - 1)) == 0) { 
        continue;
      }

      for (int j = 0;j < i;j++) { 
        if (derList.get(j).size() == outList.size()) { 
          boolean flag = true;
          ArrayList<String> mergeList = derList.get(j);
          for (int k = 0;k < outList.size() - 1;k += 2) {
            if (mergeList.indexOf(outList.get(k)) == -1) {
              flag = false;
              break;
            } else if (!mergeList.get(mergeList.indexOf(outList.get(k)) + 1).equals(outList.get(k + 1))) { 
              flag = false;
              break;
            }
          }

          if (flag) { 
            double addUp = Double.parseDouble(mergeList.get(outList.size() - 1));
            addUp += Double.parseDouble(outList.get(outList.size() - 1));
            if (addUp > 0) { 
              mergeList.set(outList.size() - 1,"+" + addUp);
            } else { 
              mergeList.set(outList.size() - 1,"" + addUp);
            }
            outList.set(outList.size() - 1,"0");
          }
        }
      }
    }

    if (counter == 0) { 
      System.out.println("no var");
      return;
    }

    cleanUp(derList);

    show(derList);
  }

  // 2 - 4x + x3y + xyz4 - 5z^5

  /**.
  * @param resultList array
  */
  public static void show(ArrayList<ArrayList<String>> resultList) { 
    for (ArrayList<String> outList:resultList) {
      String partExp = "";
      for (int i = 0;i < outList.size() - 1;i += 2) { 
        partExp += outList.get(i);

        if (!outList.get(i + 1).equals("1")) {
          partExp += "^" + outList.get(i + 1);
        }
      }

      if (outList.get(outList.size() - 1).equals("+1")) { 
        System.out.print("+" + partExp); 
      } else if (outList.get(outList.size() - 1).equals("-1")) { 
        System.out.print("-" + partExp);
      } else { 
        System.out.print(outList.get(outList.size() - 1) + partExp);
      }
    }
    System.out.println();
  }

  /**.
  * @param sourceList array
  */
  public static void cleanUp(ArrayList<ArrayList<String>> sourceList) { 
    Iterator<ArrayList<String>> sListIterator = sourceList.iterator();
    double sum = 0;
    while (sListIterator.hasNext()) { 
      ArrayList<String> remList = sListIterator.next();
      if (Double.parseDouble(remList.get(remList.size() - 1)) == 0) { 
        sListIterator.remove();
        continue;
      }
      if (remList.size() == 1) { 
        if (Double.parseDouble(remList.get(0)) != 0) { 
          sum += Double.parseDouble(remList.get(0));
        }
        sListIterator.remove();
      }
    }

    if (sum != 0 || sourceList.isEmpty()) { 
      ArrayList<String> sumList = new ArrayList<>();
      sumList.add(sum + "");
      sourceList.add(0,sumList);
    }
  }

  public static boolean isNum(String str){
    return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
  }
}
// 2 - 4x + x3y + xyz4 - 5z^5