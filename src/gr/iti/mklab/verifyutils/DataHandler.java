package gr.iti.mklab.verifyutils;

import gr.iti.mklab.verify.ItemClassifier;
import gr.iti.mklab.verify.UserClassifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * The DataHandler class provides solutions for handling the data used on the
 * training and testing procedure. It provides functions for normalizing data and
 * handling the missing values on the dataset.
 * 
 * @author Boididou Christina
 * @date 11.07.14
 */
public class DataHandler {

	private static DataHandler dhInstance;

	public static DataHandler getInstance() {
		if (dhInstance == null) {
			dhInstance = new DataHandler();
		}
		return dhInstance;
	}

	static Normalize normFilter = new Normalize();
	static Normalize normFilterUser = new Normalize();

	static FilteredClassifier model  = new FilteredClassifier();
	static FilteredClassifier model2 = new FilteredClassifier();
	static FilteredClassifier model3 = new FilteredClassifier();
	static FilteredClassifier model4 = new FilteredClassifier();
	static FilteredClassifier model5 = new FilteredClassifier();
	static FilteredClassifier model6 = new FilteredClassifier();
	static FilteredClassifier model7 = new FilteredClassifier();
	static FilteredClassifier model8 = new FilteredClassifier();
	static FilteredClassifier model9 = new FilteredClassifier();
	static FilteredClassifier model10 = new FilteredClassifier();
	static FilteredClassifier model11 = new FilteredClassifier();
	static FilteredClassifier model12 = new FilteredClassifier();
	static FilteredClassifier model13 = new FilteredClassifier();
	static FilteredClassifier model14 = new FilteredClassifier();
	static FilteredClassifier model15 = new FilteredClassifier();
	static FilteredClassifier usermodel = new FilteredClassifier();
	static FilteredClassifier usermodel2 = new FilteredClassifier();
	static FilteredClassifier usermodel3 = new FilteredClassifier();
	static FilteredClassifier usermodel4 = new FilteredClassifier();
	static FilteredClassifier usermodel5 = new FilteredClassifier();
	static FilteredClassifier usermodel6 = new FilteredClassifier();
	static FilteredClassifier usermodel7 = new FilteredClassifier();
	static FilteredClassifier usermodel8 = new FilteredClassifier();
	static FilteredClassifier usermodel9 = new FilteredClassifier();
	
	
	public static void initializeModels() {

		model = new FilteredClassifier();
		model2 = new FilteredClassifier();
		model3 = new FilteredClassifier();
		model4 = new FilteredClassifier();
		model5 = new FilteredClassifier();
		model6 = new FilteredClassifier();
		model7 = new FilteredClassifier();
		model8 = new FilteredClassifier();
		model9 = new FilteredClassifier();
		model10 = new FilteredClassifier();
		model11 = new FilteredClassifier();
		model12 = new FilteredClassifier();
		model13 = new FilteredClassifier();
		model14 = new FilteredClassifier();
		model15 = new FilteredClassifier();
		usermodel = new FilteredClassifier();
		usermodel2 = new FilteredClassifier();
		usermodel3 = new FilteredClassifier();
		usermodel4 = new FilteredClassifier();
		usermodel5 = new FilteredClassifier();
		usermodel6 = new FilteredClassifier();
		usermodel7 = new FilteredClassifier();
		usermodel8 = new FilteredClassifier();
		usermodel9 = new FilteredClassifier();
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    if (((Double)value).toString() != "NaN") {
	    	BigDecimal bd = new BigDecimal(value);
	    	bd = bd.setScale(places, RoundingMode.HALF_UP);
	    	return bd.doubleValue();
	    }
	    else {
	    	return value;
	    }
	}

	/**
	 * Normalizes the given data
	 * 
	 * @param isTrainingSet
	 *            the Instances to be normalized
	 * @param fvAttributes
	 *            List<Attribute> the list of attributes of the current dataset
	 * @return the normalized Instances
	 */
	public Normalize createNormalizationFilter(Instances isTrainingSet) {

		// set the Normalize object
		Normalize norm = new Normalize();
		try {

			// set the parameters for norm object
			norm.setInputFormat(isTrainingSet);

			// set the normalization options
			String[] options = { "-S", "1.0", "-T", "0.0" };
			norm.setOptions(options);

		} catch (Exception e) {
			System.out.println("Data Normalization filter cannot be created!");
			e.printStackTrace();
		}
		return norm;
	}

	public Instances normalizeData(Instances dataset, int classIndex,
			Normalize normFilter) {

		Instances dataset_norm = null;
		try {
			
			dataset_norm = Filter.useFilter(dataset, normFilter);
			dataset_norm.setClassIndex(classIndex);
			
		} catch (Exception e) {
			System.out
					.println("Data Normalization cannot be performed! Please check your data!");
			e.printStackTrace();
		}

		return dataset_norm;
	}

	public Instances applyRegressionModel(Instances data, ArrayList<Attribute> fvAttributes, Classifier model)
			throws Exception {


		// declare the new training set
		int size = data.size();

		Instances newData = new Instances("Rel", fvAttributes, size);

		//System.out.println(data);
		// change the class index
		// isTrainingSet.setClassIndex(index);
		int index = data.classIndex();

		// System.out.println(model);

		for (int i = 0; i < data.numInstances(); i++) {

			DenseInstance inst = (DenseInstance) data.get(i);
			
			if (inst.isMissing(index)) {
				double value = 0.0;
				try {
					
					value = model.classifyInstance((DenseInstance)data.get(i));
					
					Instance newinst = new DenseInstance(fvAttributes.size());
					for (int j = 0; j < inst.numAttributes(); j++) {
						newinst.setValue(j, inst.value(j));
					}
					newinst.setValue(index, value);

					newData.add(newinst);
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			} else {
				newData.add(inst);
			}
		}
		
		newData.setClassIndex(fvAttributes.size() - 1);
		data.setClassIndex(fvAttributes.size() - 1);
	
		return newData;
	}

	/**
	 * Applies linear regression to the missing values of the training set in
	 * order to predict their values and normalization to make the values fall
	 * into the range [0,1]. It is the case of the Item type Instances.
	 * 
	 * @param trainingSet
	 * @return Instances the transformed training set
	 * @throws Exception
	 */
	public Instances getTransformedTrainingOverall(Instances trainingSet)
			throws Exception {

		initializeModels();
		ArrayList<Attribute> fvAttributes = ItemClassifier.getFvAttributes();
		
		
		Remove rm = new Remove();
		rm.setAttributeIndices("1");
		Remove rm1 = new Remove();
		rm1.setAttributeIndices("1");
		Remove rm2 = new Remove();
		rm2.setAttributeIndices("1");
		Remove rm3 = new Remove();
		rm3.setAttributeIndices("1");
		Remove rm4 = new Remove();
		rm4.setAttributeIndices("1");
		Remove rm5 = new Remove();
		rm5.setAttributeIndices("1");
		Remove rm6 = new Remove();
		rm6.setAttributeIndices("1");
		Remove rm7 = new Remove();
		rm7.setAttributeIndices("1");
		Remove rm8 = new Remove();
		rm8.setAttributeIndices("1");
		Remove rm9 = new Remove();
		rm9.setAttributeIndices("1");
		Remove rm10 = new Remove();
		rm10.setAttributeIndices("1");
		Remove rm11 = new Remove();
		rm11.setAttributeIndices("1");
		Remove rm12 = new Remove();
		rm12.setAttributeIndices("1");
		Remove rm13 = new Remove();
		rm13.setAttributeIndices("1");
		Remove rm14 = new Remove();
		rm14.setAttributeIndices("1");
		
		
		// REGRESSION
		// wotTrust
		trainingSet.setClass(fvAttributes.get(22));
				
		LinearRegression lr = new LinearRegression();
		Instances training_regr = null;
		model.setFilter(rm);
		model.setClassifier(lr);

		try {
			model.buildClassifier(trainingSet);
			training_regr = DataHandler.getInstance().applyRegressionModel(trainingSet, fvAttributes, model);
		} catch (Exception e) {
			training_regr = trainingSet;
		}
		
		
		
		// readability
		training_regr.setClass(fvAttributes.get(25));
		LinearRegression lr2 = new LinearRegression();
		Instances training_regr2 = null;

		model2.setFilter(rm1);
		model2.setClassifier(lr2);
		try {
			model2.buildClassifier(training_regr);
			training_regr2 = DataHandler.getInstance().applyRegressionModel(
					training_regr, fvAttributes, model2);
		} catch (Exception e) {
			training_regr2 = training_regr;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		// alexa popularity
		training_regr2.setClass(fvAttributes.get(28));
		LinearRegression lr3 = new LinearRegression();
		Instances training_regr3 = null;

		model3.setFilter(rm2);
		model3.setClassifier(lr3);
		try {
			model3.buildClassifier(training_regr2);
			training_regr3 = DataHandler.getInstance().applyRegressionModel(
					training_regr2, fvAttributes, model3);
		} catch (Exception e) {
			training_regr3 = training_regr2;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		// alexa delta rank
		training_regr3.setClass(fvAttributes.get(27));
		LinearRegression lr4 = new LinearRegression();
		Instances training_regr4 = null;
		model4.setFilter(rm3);
		model4.setClassifier(lr4);
		try {
			model4.buildClassifier(training_regr3);
			training_regr4 = DataHandler.getInstance().applyRegressionModel(
					training_regr3, fvAttributes, model4);
		} catch (Exception e) {
			training_regr4 = training_regr3;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		// getNumNegSentiWords
		training_regr4.setClass(fvAttributes.get(14));
		LinearRegression lr5 = new LinearRegression();
		Instances training_regr5 = null;
		model5.setFilter(rm4);
		model5.setClassifier(lr5);

		try {
			model5.buildClassifier(training_regr4);
			training_regr5 = DataHandler.getInstance().applyRegressionModel(
					training_regr4, fvAttributes, model5);
		} catch (Exception e) {
			training_regr5 = training_regr4;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		// num slangs
		training_regr5.setClass(fvAttributes.get(19));
		LinearRegression lr6 = new LinearRegression();
		Instances training_regr6 = null;
		model6.setFilter(rm5);
		model6.setClassifier(lr6);
		try {
			model6.buildClassifier(training_regr5);
			training_regr6 = DataHandler.getInstance().applyRegressionModel(
					training_regr5, fvAttributes, model6);
		} catch (Exception e) {
			training_regr6 = training_regr5;
		}

		// alexa country rank 
		training_regr6.setClass(fvAttributes.get(26));
		LinearRegression lr7 = new LinearRegression();
		Instances training_regr7 = null;
		model7.setFilter(rm6);
		model7.setClassifier(lr7);
		try {
			model7.buildClassifier(training_regr6);
			training_regr7 = DataHandler.getInstance().applyRegressionModel(
					training_regr6, fvAttributes, model7);
		} catch (Exception e) {
			training_regr7 = training_regr6;
		}

		// System.out.println(model7);

		// getNumPosSentiWords 
		training_regr7.setClass(fvAttributes.get(13));
		LinearRegression lr8 = new LinearRegression();
		Instances training_regr8 = null;
		model8.setFilter(rm7);
		model8.setClassifier(lr8);
		try {
			model8.buildClassifier(training_regr7);
			training_regr8 = DataHandler.getInstance().applyRegressionModel(
					training_regr7, fvAttributes, model8);
		} catch (Exception e) {
			training_regr8 = training_regr7;
		}

		//alexa reach rank
		training_regr8.setClass(fvAttributes.get(29));
		LinearRegression lr9 = new LinearRegression();
		Instances training_regr9 = null;
		model9.setFilter(rm8);
		model9.setClassifier(lr9);
		try {
			model9.buildClassifier(training_regr8);
			training_regr9 = DataHandler.getInstance().applyRegressionModel(
					training_regr8, fvAttributes, model9);
		} catch (Exception e) {
			training_regr9 = training_regr8;
		}

		//getNumNouns
		training_regr9.setClass(fvAttributes.get(6));
		LinearRegression lr10 = new LinearRegression();
		Instances training_regr10 = null;
		model10.setFilter(rm9);
		model10.setClassifier(lr10);
		try {
			model10.buildClassifier(training_regr9);
			training_regr10 = DataHandler.getInstance().applyRegressionModel(
					training_regr9, fvAttributes, model10);
		} catch (Exception e) {
			training_regr10 = training_regr9;
		}
		
		//1st pron
		training_regr10.setClass(fvAttributes.get(9));
		LinearRegression lr11 = new LinearRegression();
		Instances training_regr11 = null;
		model11.setFilter(rm10);
		model11.setClassifier(lr11);
		try {
			model11.buildClassifier(training_regr10);
			training_regr11 = DataHandler.getInstance().applyRegressionModel(
					training_regr10, fvAttributes, model11);
		} catch (Exception e) {
			training_regr11 = training_regr10;
		}
		
		//2nd pron
		training_regr11.setClass(fvAttributes.get(10));
		LinearRegression lr12 = new LinearRegression();
		Instances training_regr12 = null;
		model12.setFilter(rm11);
		model12.setClassifier(lr12);
		try {
			model12.buildClassifier(training_regr11);
			training_regr12 = DataHandler.getInstance().applyRegressionModel(
					training_regr11, fvAttributes, model12);
		} catch (Exception e) {
			training_regr12 = training_regr11;
		}
		
		
		//3rd pron
		training_regr12.setClass(fvAttributes.get(11));
		LinearRegression lr13 = new LinearRegression();
		Instances training_regr13 = null;
		model13.setFilter(rm12);
		model13.setClassifier(lr13);
		try {
			model13.buildClassifier(training_regr12);
			training_regr13 = DataHandler.getInstance().applyRegressionModel(training_regr12, fvAttributes, model13);			
		} catch (Exception e) {
			training_regr13 = training_regr12;
		}
		

		
		// normalization part
		String[] options = { "-S", "1.0", "-T", "0.0" };
		normFilter.setOptions(options);
		normFilter.setInputFormat(training_regr13);

		
		Instances trainingSet_normed = DataHandler.getInstance().normalizeData(training_regr13, fvAttributes.size() - 1, normFilter);
		trainingSet_normed.setClassIndex(fvAttributes.size()-1);
		
		for (int i=0; i<trainingSet_normed.size(); i++) {
			for (int j=0; j<trainingSet_normed.get(i).numAttributes();j++) {
				if (!trainingSet_normed.get(i).attribute(j).isNominal())
				trainingSet_normed.get(i).setValue(j, round(trainingSet_normed.get(i).value(j), 6) );
			}
		}
		
		trainingSet_normed = getTrimmedInstances(trainingSet_normed);
		
		return trainingSet_normed;

	}
	
	/**
	 * Applies linear regression to the missing values of the training set in
	 * order to predict their values and normalization to make the values fall
	 * into the range [0,1]. It is the case of the User type Instances.
	 * 
	 * @param trainingSet
	 * @return Instances the transformed training set
	 * @throws Exception
	 */
	public Instances getTransformedTrainingUserOverall(Instances trainingSet) {

		initializeModels();

		ArrayList<Attribute> fvAttributes = UserClassifier.getFvAttributes();
		
		
		// remove filter in order to remove the id attribute
		Remove rm = new Remove();
		rm.setAttributeIndices("1");
		Remove rm1 = new Remove();
		rm1.setAttributeIndices("1");
		Remove rm2 = new Remove();
		rm2.setAttributeIndices("1");
		Remove rm3 = new Remove();
		rm3.setAttributeIndices("1");
		Remove rm4 = new Remove();
		rm4.setAttributeIndices("1");
		Remove rm5 = new Remove();
		rm5.setAttributeIndices("1");
		Remove rm6 = new Remove();
		rm6.setAttributeIndices("1");
		Remove rm7 = new Remove();
		rm7.setAttributeIndices("1");
		Remove rm8 = new Remove();
		rm8.setAttributeIndices("1");

		// wot trust
		trainingSet.setClass(fvAttributes.get(11));
		LinearRegression lr = new LinearRegression();
		Instances training_regr = null;
		usermodel.setFilter(rm);
		usermodel.setClassifier(lr);

		try {
			usermodel.buildClassifier(trainingSet);
			training_regr = DataHandler.getInstance().applyRegressionModel(
					trainingSet, fvAttributes, usermodel);
		} catch (Exception e) {
			training_regr = trainingSet;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		//num media content
		training_regr.setClass(fvAttributes.get(12));
		LinearRegression lr2 = new LinearRegression();
		Instances training_regr2 = null;
		usermodel2.setFilter(rm1);
		usermodel2.setClassifier(lr2);
		try {
			usermodel2.buildClassifier(training_regr);
			training_regr2 = DataHandler.getInstance().applyRegressionModel(
					training_regr, fvAttributes, usermodel2);
		} catch (Exception e) {
			training_regr2 = training_regr;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		//account age
		training_regr2.setClass(fvAttributes.get(13));
		LinearRegression lr3 = new LinearRegression();
		Instances training_regr3 = null;
		usermodel3.setFilter(rm2);
		usermodel3.setClassifier(lr3);
		try {
			usermodel3.buildClassifier(training_regr2);
			training_regr3 = DataHandler.getInstance().applyRegressionModel(
					training_regr2, fvAttributes, usermodel3);
		} catch (Exception e) {
			training_regr3 = training_regr2;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}
		
		//tweet ratio
		training_regr3.setClass(fvAttributes.get(16));
		LinearRegression lr4 = new LinearRegression();
		Instances training_regr4 = null;
		usermodel4.setFilter(rm3);
		usermodel4.setClassifier(lr4);
		try {
			usermodel4.buildClassifier(training_regr3);
			training_regr4 = DataHandler.getInstance().applyRegressionModel(
					training_regr3, fvAttributes, usermodel4);
		} catch (Exception e) {
			training_regr4 = training_regr3;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		//alexa country rank
		training_regr4.setClass(fvAttributes.get(17));
		LinearRegression lr5 = new LinearRegression();
		Instances training_regr5 = null;
		usermodel5.setFilter(rm4);
		usermodel5.setClassifier(lr5);
		try {
			usermodel5.buildClassifier(training_regr4);
			training_regr5 = DataHandler.getInstance().applyRegressionModel(
					training_regr4, fvAttributes, usermodel5);
		} catch (Exception e) {
			training_regr5 = training_regr4;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}
		
		training_regr5.setClass(fvAttributes.get(18));
		LinearRegression lr6 = new LinearRegression();
		Instances training_regr6 = null;
		usermodel6.setFilter(rm5);
		usermodel6.setClassifier(lr6);
		try {
			usermodel6.buildClassifier(training_regr5);
			training_regr6 = DataHandler.getInstance().applyRegressionModel(
					training_regr5, fvAttributes, usermodel6);
		} catch (Exception e) {
			training_regr6 = training_regr5;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}

		training_regr6.setClass(fvAttributes.get(19));
		LinearRegression lr7 = new LinearRegression();
		Instances training_regr7 = null;
		usermodel7.setFilter(rm6);
		usermodel7.setClassifier(lr7);
		try {
			usermodel7.buildClassifier(training_regr6);
			training_regr7 = DataHandler.getInstance().applyRegressionModel(
					training_regr6, fvAttributes, usermodel7);
		} catch (Exception e) {
			training_regr7 = training_regr6;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}
		
		training_regr6.setClass(fvAttributes.get(20));
		LinearRegression lr8 = new LinearRegression();
		Instances training_regr8 = null;
		usermodel8.setFilter(rm6);
		usermodel8.setClassifier(lr8);
		try {
			usermodel8.buildClassifier(training_regr6);
			training_regr8 = DataHandler.getInstance().applyRegressionModel(
					training_regr7, fvAttributes, usermodel8);
		} catch (Exception e) {
			training_regr8 = training_regr7;
			// System.out.println("not enough training instances. Linear Regression not performed!");
		}
		
		//System.out.println(training_regr9.get(0));
		
		// normalization
		normFilterUser = DataHandler.getInstance().createNormalizationFilter(training_regr8);
		Instances trainingSet_normed = DataHandler.getInstance().normalizeData(training_regr8, fvAttributes.size() - 1, normFilterUser);
		trainingSet_normed.setClassIndex(fvAttributes.size()-1);
		
		
		for (int i=0; i<trainingSet_normed.size(); i++) {
			
			for (int j=0; j<trainingSet_normed.get(i).numAttributes();j++) {
				if (!trainingSet_normed.get(i).attribute(j).isNominal())
				trainingSet_normed.get(i).setValue(j, round(trainingSet_normed.get(i).value(j), 6) );
			}
			
		}
		
		trainingSet_normed = getTrimmedInstances(trainingSet_normed);
		
		return trainingSet_normed;
	}
	
	/**
	 * Applies Linear Regression and normalization to the testing set according
	 * to the models and filters created before by the training set. It is the
	 * case of the Item type Instances.
	 * 
	 * @param testing
	 *            the Instances to be transformed
	 * @return the transformed testing set
	 * @throws Exception
	 */
	public Instances getTransformedTestingOverall(Instances testing) throws Exception {

		ArrayList<Attribute> fvAttributes = ItemClassifier.getFvAttributes();
		Instances testing_regr = null, testing_regr2 = null, testing_regr3 = null, testing_regr4 = null, testing_regr5 = null, 
				testing_regr6 = null, testing_regr7 = null, testing_regr8 = null, testing_regr9 = null, testing_regr10 = null,
						testing_regr11 = null, testing_regr12 = null, testing_regr13 = null, testing_regr14 = null, testing_regr15 = null;
		
		// regression
		if (!model.toString().contains("No model built yet.")) {
			testing.setClass(fvAttributes.get(22));
			testing_regr = DataHandler.getInstance().applyRegressionModel(
					testing, fvAttributes, model);
		} else {
			testing_regr = testing;
		}

		if (!model2.toString().contains("No model built yet.")) {
			testing_regr.setClass(fvAttributes.get(25));
			testing_regr2 = DataHandler.getInstance().applyRegressionModel(
					testing_regr, fvAttributes, model2);
		} else {
			testing_regr2 = testing_regr;
		}

		if (!model3.toString().contains("No model built yet.")) {
			testing_regr2.setClass(fvAttributes.get(28));
			testing_regr3 = DataHandler.getInstance().applyRegressionModel(
					testing_regr2, fvAttributes, model3);
		} else {
			testing_regr3 = testing_regr2;
		}

		if (!model4.toString().contains("No model built yet.")) {
			testing_regr3.setClass(fvAttributes.get(27));
			testing_regr4 = DataHandler.getInstance().applyRegressionModel(
					testing_regr3, fvAttributes, model4);
		} else {
			testing_regr4 = testing_regr3;
		}

		if (!model5.toString().contains("No model built yet.")) {
			testing_regr4.setClass(fvAttributes.get(14));
			testing_regr5 = DataHandler.getInstance().applyRegressionModel(
					testing_regr4, fvAttributes, model5);
		} else {
			testing_regr5 = testing_regr4;
		}

		if (!model6.toString().contains("No model built yet.")) {
			testing_regr5.setClass(fvAttributes.get(19));
			testing_regr6 = DataHandler.getInstance().applyRegressionModel(
					testing_regr5, fvAttributes, model6);
		} else {
			testing_regr6 = testing_regr5;
		}

		if (!model7.toString().contains("No model built yet.")) {
			testing_regr6.setClass(fvAttributes.get(26));
			testing_regr7 = DataHandler.getInstance().applyRegressionModel(
					testing_regr6, fvAttributes, model7);
		} else {
			testing_regr7 = testing_regr6;
		}

		if (!model8.toString().contains("No model built yet.")) {
			testing_regr7.setClass(fvAttributes.get(13));
			testing_regr8 = DataHandler.getInstance().applyRegressionModel(
					testing_regr7, fvAttributes, model8);
		} else {
			testing_regr8 = testing_regr7;
		}

		if (!model9.toString().contains("No model built yet.")) {
			testing_regr8.setClass(fvAttributes.get(29));
			testing_regr9 = DataHandler.getInstance().applyRegressionModel(
					testing_regr8, fvAttributes, model9);
		} else {
			testing_regr9 = testing_regr8;
		}
		
		if (!model10.toString().contains("No model built yet.")) {
			testing_regr9.setClass(fvAttributes.get(6));
			testing_regr10 = DataHandler.getInstance().applyRegressionModel(
					testing_regr9, fvAttributes, model10);
		} else {
			testing_regr10 = testing_regr9;
		}
		
		if (!model11.toString().contains("No model built yet.")) {
			testing_regr10.setClass(fvAttributes.get(9));
			testing_regr11 = DataHandler.getInstance().applyRegressionModel(
					testing_regr10, fvAttributes, model11);
		} else {
			testing_regr11 = testing_regr10;
		}
		
		if (!model12.toString().contains("No model built yet.")) {
			testing_regr11.setClass(fvAttributes.get(10));
			testing_regr12 = DataHandler.getInstance().applyRegressionModel(
					testing_regr11, fvAttributes, model12);
		} else {
			testing_regr12 = testing_regr11;
		}
		
		if (!model13.toString().contains("No model built yet.")) {
			testing_regr12.setClass(fvAttributes.get(11));
			testing_regr13 = DataHandler.getInstance().applyRegressionModel(
					testing_regr12, fvAttributes, model13);
		} else {
			testing_regr13 = testing_regr12;
		}
		
		
		
		// normalization
		Instances testingSet_normed = DataHandler.getInstance().normalizeData(testing_regr13, fvAttributes.size() - 1, normFilter);
		testingSet_normed.setClassIndex(fvAttributes.size()-1);
		
		// testingSet_normed = getTrimmedInstances(testingSet_normed);
		for (int i=0; i<testingSet_normed.size(); i++) {
			for (int j=0; j<testingSet_normed.get(i).numAttributes();j++) {
				if (!testingSet_normed.get(i).attribute(j).isNominal())
				testingSet_normed.get(i).setValue(j, round(testingSet_normed.get(i).value(j), 6) );
			}
		}
		
		testingSet_normed = getTrimmedInstances(testingSet_normed);
		
		
		return testingSet_normed;
	}
	
	
	/**
	 * Applies Linear Regression and normalization to the testing set according
	 * to the models and filters created before by the training set. It is the
	 * case of the User type Instances.
	 * 
	 * @param testing
	 *            the Instances to be transformed
	 * @return the transformed testing set
	 * @throws Exception
	 */
	public Instances getTransformedTestingUserOverall(Instances testing)
			throws Exception {

		ArrayList<Attribute> fvAttributes = UserClassifier.getFvAttributes();
		Instances testing_regr = null, testing_regr2 = null, testing_regr3 = null, testing_regr4 = null, testing_regr5 = null,
				testing_regr6 = null, testing_regr7 = null, testing_regr8 = null;

		// testing_regr3 = testing;

		if (!usermodel.toString().contains("No model built yet.")) {
			testing.setClass(fvAttributes.get(11));
			testing_regr = DataHandler.getInstance().applyRegressionModel(
					testing, fvAttributes, usermodel);
		} else {
			testing_regr = testing;
		}
		if (!usermodel2.toString().contains("No model built yet.")) {
			testing_regr.setClass(fvAttributes.get(12));
			testing_regr2 = DataHandler.getInstance().applyRegressionModel(
					testing_regr, fvAttributes, usermodel2);
		} else {
			testing_regr2 = testing_regr;
		}
		if (!usermodel3.toString().contains("No model built yet.")) {
			testing_regr2.setClass(fvAttributes.get(13));
			testing_regr3 = DataHandler.getInstance().applyRegressionModel(
					testing_regr2, fvAttributes, usermodel3);
		} else {
			testing_regr3 = testing_regr2;
		}

		if (!usermodel4.toString().contains("No model built yet.")) {
			testing_regr3.setClass(fvAttributes.get(16));
			testing_regr4 = DataHandler.getInstance().applyRegressionModel(
					testing_regr3, fvAttributes, usermodel4);
		} else {
			testing_regr4 = testing_regr3;
		}

		if (!usermodel5.toString().contains("No model built yet.")) {
			testing_regr4.setClass(fvAttributes.get(17));
			testing_regr5 = DataHandler.getInstance().applyRegressionModel(
					testing_regr4, fvAttributes, usermodel5);
		} else {
			testing_regr5 = testing_regr4;
		}
		
		if (!usermodel6.toString().contains("No model built yet.")) {
			testing_regr5.setClass(fvAttributes.get(18));
			testing_regr6 = DataHandler.getInstance().applyRegressionModel(
					testing_regr5, fvAttributes, usermodel6);
		} else {
			testing_regr6 = testing_regr5;
		}

		if (!usermodel7.toString().contains("No model built yet.")) {
			testing_regr6.setClass(fvAttributes.get(19));
			testing_regr7 = DataHandler.getInstance().applyRegressionModel(
					testing_regr6, fvAttributes, usermodel7);
		} else {
			testing_regr7 = testing_regr6;
		}
		
		if (!usermodel8.toString().contains("No model built yet.")) {
			testing_regr7.setClass(fvAttributes.get(20));
			testing_regr8 = DataHandler.getInstance().applyRegressionModel(
					testing_regr7, fvAttributes, usermodel7);
		} else {
			testing_regr8 = testing_regr7;
		}
		
		
		// normalization
		Instances testingSet_normed = DataHandler.getInstance().normalizeData(
				testing_regr8, fvAttributes.size() - 1, normFilterUser);
		testingSet_normed.setClassIndex(fvAttributes.size()-1);

		for (int i=0; i<testingSet_normed.size(); i++) {
			//System.out.println(testingSet_normed.get(i));
			for (int j=0; j<testingSet_normed.get(i).numAttributes();j++) {
				if (!testingSet_normed.get(i).attribute(j).isNominal())
				testingSet_normed.get(i).setValue(j, round(testingSet_normed.get(i).value(j), 6) );
			}
			//System.out.println(testingSet_normed.get(i));
		}
		
		testingSet_normed = getTrimmedInstances(testingSet_normed);
		
		return testingSet_normed;
	}
	
	public Instances getTrimmedInstances(Instances data) {
		
		for (int i=0;i<data.size();i++) {
			for (int j=0; j<data.instance(i).numValues(); j++) {
				if (!data.instance(i).attribute(j).isString()) {
					if (data.instance(i).value(j) < 0) {
						data.instance(i).setValue(j, 0);
					}
					else if (data.instance(i).value(j) > 1) {
						data.instance(i).setValue(j, 1);
					}
				}
			}
		}
		return data;
		}

}
