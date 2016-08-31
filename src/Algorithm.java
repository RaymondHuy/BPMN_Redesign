import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.text.Document;

import java.sql.Date;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.moon.logic.ClassicalLogic;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;

public class Algorithm {
	public Formula makeFormulaFromArrayList(ArrayList<String> arrStr) {
		String exp = "";
		boolean firstTime = true;
		for (String element : arrStr) {
			if (firstTime)
				exp += element;
			else
				exp += '&' + element;
			firstTime = false;
		}

		try {
			return (Formula) logic.createExpression(exp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean inconsistant(Formula f1, Formula f2, Formula[] f2Collection) {
		Formula fCombination = f1;
		Formula[] ax = {};
		for (Formula formula : f2Collection) {
			fCombination.and(formula);
		}
		return logic.inference().infer(ax, fCombination.not());
	}

	public boolean entail(Formula[] fCollection, Formula f0) throws Exception {

		boolean deduce = logic.inference().infer(fCollection, f0);
		// System.out.println("A entail B? " + deduce);
		return deduce;
	}

	public Formula acc(Formula f1, ArrayList<String> arrf1, Formula f2, Formula[] f2Collection) throws Exception {
		ArrayList<String> arr = arrf1;
		if (inconsistant(f1, f2, f2Collection)) {
			Formula strFormula;
			for (String str : arrf1) {
				strFormula = (Formula) logic.createExpression(str);
				if (inconsistant(strFormula, f2, f2Collection)) {
					arr.remove(str);
				}
			}
		}
		f1 = makeFormulaFromArrayList(arr);
		Formula facc = f2.and(f1);
		return facc;
	}

	static public Logic logic = new ClassicalLogic();

	public void elimination(String strCond, String strConsqnt, int[] Label, BusinessTask[] tList) throws Exception {

		long startedTime = System.currentTimeMillis();

		Formula Cond = (Formula) logic.createExpression(strCond);
		Formula Consqnt = (Formula) logic.createExpression(strConsqnt);
		Formula f1 = (Formula) logic
				.createExpression("ContractAtPreRentConditionEqualSoundCondition => ContractAtPreRentCondition");
		Formula[] fCollection = new Formula[] { Consqnt, f1 };

		Formula f3 = (Formula) logic.createExpression("ContractAtCarAgeEqualOrBiggerThan2 => ContractAtCarAge");
		Formula f4 = (Formula) logic.createExpression("CarAtLevelEqualLuxury => CarAtLevel");
		Formula f5 = (Formula) logic.createExpression("ContractAtCarMileageSmallerThan30000 => ContractAtCarMileage");

		int[] lstTarget = new int[7];
		int[] iReduce = new int[7];
		ArrayList lstRemain = new ArrayList();
		int index = 0;
		Formula facc;
		int[] lstExcluded = new int[7];
		boolean flag = true;
		BusinessTask[] tRemain = new BusinessTask[7];

		for (int i : Label) {
			facc = acc(tList[i].getInstance(), tList[i].getinstance_(), tList[i].getPreCondition(),
					new Formula[] { tList[i].getPreCondition(), f3, f4, f5 });
			// facc = acc(new Formula[]{tList[i].getInstance(), f3, f4, f5},
			// tList[i].getInstance(), tList[i].getPreCondition());
			if (entail(new Formula[] { facc }, Cond) && entail(fCollection, tList[i].getPostComma())) {
				lstTarget[index] = i;
				lstRemain.add(i);
				index++;
			}
		}
		if (lstTarget[0] > 0) {
			for (int i : lstTarget) {
				if (i > 0) {
					index = 1;
					for (int k : lstTarget) {
						if ((k > 0) && (k != i)) {
							lstExcluded[index] = k;
							index++;
						}
					}
					index = 1;
					for (int j : lstExcluded) {
						if (j > 0) {
							if (entail(new Formula[] { tList[i].getPostCondition() }, tList[j].getPostCondition())) {
								flag = true;
								for (int m : iReduce) {
									if ((m > 0) && (j == m)) {
										flag = false;
									}
								}
								if (flag == true) {
									Eliminate(tList[j]);
									System.out.println(tList[j].name + " task is eliminated.");
									iReduce[index] = j;
									lstRemain.remove(j);
									index++;
								}
							}
						}
					}

				}
			}
			System.out.println(
					"These following tasks are remained but some tasks will be reduced a part of, based on the user choices.");
			for (int i = 1; i <= lstRemain.size(); i++) {
				index = (Integer) lstRemain.get(i - 1);
				tRemain[i] = tList[index];
				System.out.print(tList[index].name + ", ");
			}
			System.out.println("In this case, we will reduce a part of Service Booked Vehilces task.");
			ReduceTask(tRemain);
		}
		long processingTime = System.currentTimeMillis() - startedTime;
		System.out.println("Processing time is: " + processingTime + " milliseconds.");

	}

	public void ReduceTask(BusinessTask[] tRemain) throws Exception {

	}

	public void Eliminate(BusinessTask tList) throws Exception {

	}

	public void reordering(String strCond, String strConsqnt, int[] Label, BusinessTask[] tList) throws Exception {
		// System.out.println("Started time: " + System.currentTimeMillis());
		long startedTime = System.currentTimeMillis();

		Formula Cond = (Formula) logic.createExpression(strCond);
		Formula Consqnt = (Formula) logic.createExpression(strConsqnt);
		Formula f1 = (Formula) logic
				.createExpression("ContractAtPostRentAssessmentEqualAvoided => ContractAtApprovalEqualDenied");
		Formula f2 = (Formula) logic.createExpression("ContractAtApprovalEqualDenied => ContractAtApproval");
		Formula[] fCollection = new Formula[] { Consqnt, f1, f2 };

		Formula f3 = (Formula) logic.createExpression("CustomerAtAgeIn18To30 => CustomerAtAge");
		Formula f4 = (Formula) logic.createExpression("CustomerAtGenderEqualMale => CustomerAtGender");
		Formula f5 = (Formula) logic.createExpression("CustomerAtJobEqualUnidentified => CustomerAtJob");
		Formula f6 = (Formula) logic
				.createExpression("CustomerAtDriverLicensePeriodIn0To2 => CustomerAtDriverLicensePeriod");
		Formula f7 = (Formula) logic
				.createExpression("ContractAtInsuranceTypeEqualBreakdown => ContractAtInsuranceType");
		Formula f8 = (Formula) logic.createExpression("CarAtLevelEqualLuxury => CarAtLevel");
		Formula f9 = (Formula) logic.createExpression("CarAtModelEqualX6 => CarAtModel");

		Formula facc1, facc2;
		BusinessTask tTarget = new BusinessTask();
		int index = 0;
		for (int i : Label) {
			if (entail(fCollection, tList[i].getPostComma())) {
				tTarget = tList[i];
				index = i;
				break;
			}
		}
		if (index > 0) {
			for (int i : Label) {
				if (i > 1 && i < index) {
					facc1 = acc(tList[i - 1].getPostCondition(), tList[i - 1].getpost_cond(),
							tList[i].getPostCondition(), (new Formula[] { tList[i].getPostCondition() }));
					facc2 = acc(tTarget.getDom(), tTarget.getdom_(), facc1,
							new Formula[] { facc1, f3, f4, f5, f6, f7, f8, f9 });
					// facc1 = acc(new Formula[]{tList[i-1].getPostCondition()}
					// , tList[i-1].getPostCondition(),
					// tList[i].getPostCondition());
					// facc2 = acc(new Formula[]{tTarget.getDom(), f3, f4, f5,
					// f6, f7, f8, f9}, tTarget.getDom(), facc1);
					if (entail(new Formula[] { facc2 }, Cond)) {
						System.out.println("Task " + tTarget.name
								+ " will be decomposed to a new task is: Task Check rent request");
						System.out.println("New task is located after task " + tList[i].name);
						DecomposeAndReorderTask(tTarget);
						break;
					}
				}
			}
		}
		long processingTime = System.currentTimeMillis() - startedTime;
		System.out.println("Processing time is: " + processingTime + " milliseconds.");
	}

	public void DecomposeAndReorderTask(BusinessTask task) {

	}

}
