package com.example;

//import java.awt.MenuBar;

//import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


/**.
  * @param args []
 */

public class MyClass {
  /**.
  * @param args []
 */
  private static final Logger LOG = Logger.getLogger(MyClass.class);

  /**.
  * @param args []
 */
  
  private static ArrayList<ArrayList<String>> expList = new ArrayList<>();
  /**.
   * @param args []
  */
  
  public static final void main (final String[] args) { 
    BasicConfigurator.configure();
   
    final Scanner scip = new Scanner(System.in);
    String getLine;
   
    do { 
      //System.out.print("->");
      LOG.info("->");
      getLine = scip.nextLine();

      if (getLine.charAt(0 ) == '!') { 
        if (getLine.startsWith("!simplify")) { 
          simplify(getLine); 
        } else if (getLine.startsWith("!d/d")) { 
          derivative(getLine); 
        } else { 
          //System.out.println("The wrong command");
          LOG.info("The wrong command");
        }
      } else if (getLine.matches(".*[(|)]*.*]]")) {
        //System.out.println("Wrong expression");
        LOG.info("Wrong expression");
      } else { 
        expression(getLine.trim());
      }
      
    } while (!("exit").equals(getLine));
    //System.out.println("________________");
    LOG.info("________________");
    //System.exit(0);
  }

  // 2 - 4x + x3y + xyz4 - 5z^5

  // !simplify x=1 y=2 z=3
  /**.
  * 
  *
  * @author George Bush
  */
  
  public final static void expression(final String exp) { 
    expList.clear();
    final ArrayList<String> tempList = new ArrayList<>();
    String number = "";
    int adder = 1;
    boolean isNegetive = false;
    final char[] expCharArray = exp.toCharArray();
    for (int i = 0;i < exp.length();i++) { 
      final char chor = expCharArray[i];

      if (Character.isDigit(chor)) {
        number += chor;
      } else if (Character.isLetter(chor)) {
        String para = String.valueOf(chor);
        if (tempList.indexOf(chor) == -1) { 
          tempList.add(para);
          tempList.add("1");
        } else { 
          final  int expUp = Integer.parseInt(tempList.get(tempList.indexOf(chor) + 1)) + 1;
          tempList.set(tempList.indexOf(chor) + 1, String.valueOf(expUp));
        }
        if (i != exp.length() - 1 && expCharArray[i + 1] == '^') { 
          ++i;
          ++i;
          para = "";
          while (i < exp.length() && Character.isDigit(expCharArray[i])) { 
            para += expCharArray[i++];
          }
          i--;
          if (tempList.indexOf(chor) == -1) { 
            tempList.add(para);
          } else { 
            final int expUp = Integer.parseInt(tempList.get(tempList.indexOf(chor) + 1))
                                + Integer.parseInt(para) - 1;
            tempList.set(tempList.indexOf(chor) + 1, String.valueOf(expUp));
          }
        }

        if (!("").equals(number)) { 
          adder *= Integer.parseInt(number); 
        } 
        number = "";
      }

      if (i == exp.length() - 1 || chor == '+' || chor == '-') { 

        if (i == 0 && chor == '-') {
          isNegetive = true;
          continue;
        }

        if (!("").equals(number)) { 
          adder *= Integer.parseInt(number);
        }
        if (isNegetive) { 
          tempList.add(String.valueOf(adder * - 1));
        } else { 
          tempList.add("+" + adder); 
        }
        if (!tempList.isEmpty()) { 
          expList.add(new ArrayList<>(tempList));
        }

        tempList.clear();
        adder = 1;
        isNegetive = chor == '-';//are you OK?
        number = "";
      }
    }

    show(expList);
  }

  /**.
  * @param menbers string
  */
  public final static void simplify(final String menbers) { 
    //BasicConfigurator.configure();
    //Logger log1 = Logger.getLogger(MyClass.class.getName());
    final Scanner scip = new Scanner(menbers);
    scip.next();
    if (!scip.hasNext()) { 
      show(expList);
      return;
    }

    final ArrayList<ArrayList<String>> simList  = new ArrayList<>(expList);

    while (scip.hasNext()) {
      final String[] equation = scip.next().split("=");
      int huji = 1;
      if (equation.length == huji) { 
        //System.out.println("wrong format");
        LOG.info("wrong format");
        return;
      }
      final String var = equation[0].trim();
      final Double value = Double.parseDouble(equation[1]);

      int counter = 0;

      for (int i = 0;i < simList.size();i++) { 
        final  ArrayList<String> outList = simList.get(i);
        if (outList.indexOf(var) != -1) {  
          counter++;
          Double expUp = Double.parseDouble(outList.get(outList.indexOf(var) + 1));
          expUp = Math.pow(value,expUp) * Double.parseDouble(outList.get(outList.size() - 1));

          if (expUp > 0) { 
            outList.set(outList.size() - 1, "+" + expUp);
          } else { 
            outList.set(outList.size() - 1, String.valueOf(expUp)); 
          } 
          outList.remove(outList.indexOf(var) + 1);
          outList.remove(outList.indexOf(var));
        }
        final int yyu = 1;
        if (outList.size() == yyu) { 
          continue;
        }
        if (Double.parseDouble(outList.get(outList.size() - 1)) == 0) { 
          continue;
        }
        for (int j = 0;j < i;j++) {
          if (simList.get(j).size() == outList.size()) { 
            boolean flag = true;
            final ArrayList<String> mergeList = simList.get(j);
            for (int k = 0;k < outList.size() - 1;k += 2) { 
              if (mergeList.indexOf(outList.get(k)) == -1) {
                flag = false;
                break;
              } else if(!mergeList.get(mergeList.indexOf(outList.get(k)) + 1).equals(outList.get(k + 1))) { 
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
                mergeList.set(outList.size() - 1,String.valueOf(addUp));
              }
              outList.set(outList.size() - 1,"0");
            }
          }
        }
      }
      if (counter == 0) { 
        //System.out.println("no var");
        LOG.info("no var");
        return;
      }
    }
    
    cleanUp(simList);

    show(simList);
  }

  /**.
  * @param menbers string
  */
  public final static void derivative(final  String menbers) { 
    final Scanner scip = new Scanner(menbers);
    scip.next();
    if (!scip.hasNext()) { 
      LOG.info("wrong format");
      return;
    }

    String der;
    der = scip.next();

    if (scip.hasNext()) { 
     
      LOG.info("wrong format");
      return;
    }

    if (expList.isEmpty()) { 
      
      LOG.info("The current expression is empty");
      return;
    }

    final ArrayList<ArrayList<String>> derList  = new ArrayList<>(expList);
    //深拷贝不管用 这是BUG 我要报警

    int counter = 0;

    for (int i = 0;i < derList.size();i++) {
      final  ArrayList<String> outList = derList.get(i);

      if (outList.indexOf(der) == -1) { 
        outList.set(outList.size() - 1, "0");
        continue;
      } else { 
        counter++;
        int expUp = Integer.parseInt(outList.get(outList.indexOf(der) + 1));
        final int euii = 1;
        if (expUp == euii) { 
          outList.remove(outList.indexOf(der) + 1);
          outList.remove(outList.indexOf(der));
        } else {
          outList.set(outList.indexOf(der) + 1, String.valueOf(expUp - 1));
          expUp *= Integer.parseInt(outList.get(outList.size() - 1));

          if (expUp > 0) { 
            outList.set(outList.size() - 1, "+" + expUp);
          } else { 
            outList.set(outList.size() - 1, String.valueOf(expUp));
          }
        }
      }
      final int siii = 1;
      if (outList.size() == siii) { 
        continue;
      }
      if (Double.parseDouble(outList.get(outList.size() - 1)) == 0) { 
        continue;
      }

      for (int j = 0;j < i;j++) { 
        if (derList.get(j).size() == outList.size()) { 
          boolean flag = true;
          final ArrayList<String> mergeList = derList.get(j);
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
              mergeList.set(outList.size() - 1,String.valueOf(addUp));
            }
            outList.set(outList.size() - 1,"0");
          }
        }
      }
    }

    if (counter == 0) { 
      //System.out.println("no var");
      LOG.info("no var");
      return;
    }

    cleanUp(derList);

    show(derList);
  }

  // 2 - 4x + x3y + xyz4 - 5z^5

  /**.
  * @param resultList array
  */
  public static void show(final ArrayList<ArrayList<String>> resultList) { 
    int outL;
    BasicConfigurator.configure();
    //Logger log = Logger.getLogger(MyClass.class.getName());
    for (final ArrayList<String> outList:resultList) {
      String partExp = "";
      for (int i = 0;i < outList.size() - 1;i += 2) { 
        partExp += outList.get(i);

        if (!outList.get(i + 1).equals("1")) {
          partExp += "^" + outList.get(i + 1);
        }
      }

      if (outList.get(outList.size() - 1).equals("+1")) { 
        //System.out.print("+" + partExp); 
        LOG.info("+");
        LOG.info(partExp);
      } else if (outList.get(outList.size() - 1).equals("-1")) { 
        //System.out.print("-" + partExp);
        LOG.info("-");
        LOG.info(partExp);
      } else {  
        outL = outList.size() - 1;
        //System.out.print(outList.get(outList.size() - 1) + partExp);
        LOG.info(outList.get(outL));
        LOG.info(partExp);
      }
    }
    //System.out.println();
    LOG.info("\n");
  }

  /**.
  * @param sourceList array
  */
  public final static void cleanUp(final ArrayList<ArrayList<String>> sourceList) { 
    final Iterator<ArrayList<String>> sListIterator = sourceList.iterator();
    double sum = 0;
    while (sListIterator.hasNext()) { 
      final  ArrayList<String> remList = sListIterator.next();
      if (Double.parseDouble(remList.get(remList.size() - 1)) == 0) { 
        sListIterator.remove();
        continue;
      }
      int yyii = 1;
      if (remList.size() == yyii) { 
        if (Double.parseDouble(remList.get(0)) != 0) { 
          sum += Double.parseDouble(remList.get(0));
        }
        sListIterator.remove();
      }
    }

    if (sum != 0 || sourceList.isEmpty()) { 
      final ArrayList<String> sumList = new ArrayList<>();
      sumList.add(String.valueOf(sum));
      sourceList.add(0,sumList);
    }
  }
  /**.
  * 
  *
  * @author George Bush
  */
  
  public final static boolean isNum(final String str){
    return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
  }
}
// 2 - 4x + x3y + xyz4 - 5z^5