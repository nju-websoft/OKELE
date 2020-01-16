package algorithm.mult;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import common.Constants;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

import java.util.*;

public class MBM extends Fusioner {
    public MBM(DataSet dataSet, FusionerParameters params) {
        super(dataSet, params); 
    }

    private static boolean _x_flag = true;

   
    HashMap<Integer, Set<String>> source_groups; 
    HashMap<String,HashMap<Integer, Set<Integer>>> value_groups; 
    HashMap<String,HashMap<Integer,Set<Integer>>> source_value_map; 
    HashMap<String, HashMap<Integer,Set<Integer>>> value_source_map;

    HashMap<String,HashMap<Integer, Integer>> value_group_id; 
    HashMap<String, Integer> source_group_id; 


    private static double copy_proba_blind = 0.8;  
    private static double copy_proba_smart_t = 0.85;
    private static double copy_proba_smart_f = 0.7;

    HashMap<Integer,Double> t_p_p; 
    HashMap<Integer,Double> t_n_p; 

    HashMap<Integer,Double> t_p_p_old;
    HashMap<Integer,Double> t_n_p_old;

    HashMap<String,HashMap<Integer,Double>> veracity_score_value; 

    HashMap<String,HashMap<Integer,HashMap<Integer,Double>>> degree_source_value;

    HashMap<String,HashMap<Integer,HashMap<Integer, Double>>> conf_source_value;

    private boolean copy_blind_flag = false; 
    HashMap<String,HashMap<Integer,Double>> p_a_c_value; 


    private void Grouping_sources_values(){

        HashMap<String,Set<Integer>> source_values = new HashMap<>();
        Set<Integer> values = dataSet.getDataItemValueMap().keySet();
        for(int value:values){
            Set<String> source_value = dataSet.getDataItemValue(value).getSources(); 
            for(String sou:source_value){
                if(source_values.containsKey(sou)){
                    source_values.get(sou).add(value);
                }else{
                    source_values.put(sou,new HashSet<>(Arrays.asList(value)));
                }
            }
        }
        this.source_groups = merge(source_values);

        value_groups = new HashMap<>();
        for(String item_id:dataSet.getDataItemMap().keySet()){
            HashMap<Integer,Set<String>> value_source = new HashMap<>();
            DataItem item = dataSet.getDataItem(item_id);
            for(DataItemValue itemValue:item.getValues()){
                value_source.put(itemValue.getValueIdentifier(),itemValue.getSources());
            }
            this.value_groups.put(item_id,merge(value_source));
        }

		
        this.source_group_id = new HashMap<>();
        for(int sourceid:source_groups.keySet())
            for(String source: source_groups.get(sourceid))
                source_group_id.put(source,sourceid);

        this.value_group_id = new HashMap<>();
        for(String item_id:value_groups.keySet()) {
            HashMap<Integer, Integer> v_g_i_temp = new HashMap<>();
            for(int valueid:value_groups.get(item_id).keySet()) {
                for (Integer value : value_groups.get(item_id).get(valueid))
                    v_g_i_temp.put(value, valueid);
            }
            this.value_group_id.put(item_id,v_g_i_temp);
        }

        this.source_value_map = new HashMap<>();
        this.value_source_map = new HashMap<>();
        for(String item_id:dataSet.getDataItemMap().keySet()) {
            if(source_value_map.containsKey(item_id)){
                continue;
            }
            HashMap<Integer, Integer> v_g_id = this.value_group_id.get(item_id);
            HashMap<Integer,Set<Integer>> source_value_temp = new HashMap<>();
            HashMap<Integer,Set<Integer>> value_source_temp = new HashMap<>();

            for (String source : dataSet.getSourceMap().keySet()) {
                int sourceid_temp = this.source_group_id.get(source);
                if (source_value_temp.containsKey(sourceid_temp)) {
                    continue;
                }
                Set<Integer> value_ids = new HashSet<>();
                Source s_source = dataSet.getSourceMap().get(source);
                if(s_source.getDataItemValues(item_id)==null){
                    continue;
                }
                for (Integer value : s_source.getDataItemValues(item_id)) {
                    int valueid_temp = v_g_id.get(value);
                    value_ids.add(valueid_temp);
                }
                source_value_temp.put(sourceid_temp, value_ids);

                for (int valueid : value_ids) {
                    if (value_source_temp.containsKey(valueid)) {
                        value_source_temp.get(valueid).add(sourceid_temp);
                    } else {
                        value_source_temp.put(valueid, new HashSet<>(Arrays.asList(sourceid_temp)));
                    }
                }
            }
            source_value_map.put(item_id,source_value_temp);
            value_source_map.put(item_id,value_source_temp);
        }
    }


    private void init(){
        p_a_c_value = new HashMap<>();
        for(String itemid:dataSet.getDataItemMap().keySet()){
            HashMap<Integer,Double> temp_v_s = new HashMap<>();
            for(int groupid:value_groups.get(itemid).keySet()) {
                temp_v_s.put(groupid, 0.5);
            }
            p_a_c_value.put(itemid,temp_v_s);
        }

        this.t_p_p = new HashMap<>();
        this.t_n_p = new HashMap<>();
        t_p_p_old = new HashMap<>();
        t_n_p_old = new HashMap<>();
        this.veracity_score_value = new HashMap<>();
        for(int groupid:source_groups.keySet()){
            t_p_p.put(groupid,0.8);
            t_n_p.put(groupid,0.7);
        }

        for(String itemid:dataSet.getDataItemMap().keySet()){
            HashMap<Integer,Double> temp_v_s = new HashMap<>();
            for(int groupid:value_groups.get(itemid).keySet()) {
                temp_v_s.put(groupid, 0.5);
            }
            veracity_score_value.put(itemid,temp_v_s);
        }

        degree_source_value = new HashMap<>();
        for(String itemid:dataSet.getDataItemMap().keySet()) {
            HashMap<Integer,HashMap<Integer,Double>> temp = new HashMap<>(); 
            for (int s_group_id : source_value_map.get(itemid).keySet()) {
                HashMap<Integer, Double> v_id_cnt = new HashMap<>();
//                for(int v_id:value_source_map.get(itemid).keySet()){ 
//                    v_id_cnt.put(v_id, 0.1);
//                }
                double g = source_groups.get(s_group_id).size(); 
                for(int v_group_id:value_groups.get(itemid).keySet()){
//                for (int v_group_id : source_value_map.get(itemid).get(s_group_id)) {
                    v_id_cnt.put(v_group_id, g);
                }
                temp.put(s_group_id, v_id_cnt);
            }
            degree_source_value.put(itemid,temp);
        }

        conf_source_value = new HashMap<>();
        for(String itemid:dataSet.getDataItemMap().keySet()) {
            HashMap<Integer,HashMap<Integer,Double>> temp = new HashMap<>();
            for (int s_group_id : source_value_map.get(itemid).keySet()) {
                Set<Integer> value_id = source_value_map.get(itemid).get(s_group_id);
                int value_size = value_source_map.get(itemid).keySet().size();
                HashMap<Integer, Double> v_id_s = new HashMap<>();
                for (int v_group_id : value_source_map.get(itemid).keySet()) { 
                    double score = 0.0;
                    if (value_id.contains(v_group_id)) {
                        score = 1.0 / (value_id.size()) * (1 - 1.0 / value_size);
                    } else {
                        score = 1.0 / (value_size - value_id.size()) / value_size;
                    }
                    v_id_s.put(v_group_id, score);
                }
                temp.put(s_group_id, v_id_s);
            }
            conf_source_value.put(itemid,temp);
        }
    }

    private HashMap<String, HashMap<Integer,HashMap<Integer, Double>>> f_g_cs = new HashMap<>();
    private HashMap<String, HashMap<Integer,HashMap<Integer, Double>>> j_g_cs = new HashMap<>();
    private HashMap<String, HashMap<Integer,HashMap<Integer, Double>>> _f_g_cs = new HashMap<>();
    private HashMap<String, HashMap<Integer,HashMap<Integer, Double>>> _j_g_cs = new HashMap<>();

    private double last = 0;
    @SuppressWarnings("unchecked")
	public int run(){
        Grouping_sources_values();
//        System.out.println(value_groups.toString());
        init();

        boolean continueComputation = true;
        double newTrustworthinessCosineSimilarity;
        int iterationCount = 0;

        while (iterationCount < Constants.maxIterationCount) {
            iterationCount++;

            for(String dataitem:dataSet.getDataItemMap().keySet()){ 
                HashMap<Integer,HashMap<Integer, Double>> f_g_cs_temp = f_g_cs.containsKey(dataitem)?f_g_cs.get(dataitem): new HashMap<Integer,HashMap<Integer, Double>>();
                HashMap<Integer,HashMap<Integer, Double>> j_g_cs_temp = j_g_cs.containsKey(dataitem)?j_g_cs.get(dataitem):new  HashMap<Integer,HashMap<Integer, Double>>();
                HashMap<Integer,HashMap<Integer, Double>> _f_g_cs_temp = _f_g_cs.containsKey(dataitem)?_f_g_cs.get(dataitem):new HashMap<Integer,HashMap<Integer, Double>>();
                HashMap<Integer,HashMap<Integer, Double>> _j_g_cs_temp = _j_g_cs.containsKey(dataitem)?_j_g_cs.get(dataitem):new HashMap<Integer,HashMap<Integer, Double>>();
                for(int value_group_id:value_groups.get(dataitem).keySet()){ 

                    for(int source_group_id:source_value_map.get(dataitem).keySet()){
                        double w_g_c = degree_source_value.get(dataitem).get(source_group_id).get(value_group_id);
                        double l_g_c =  infer_source_dependency(dataitem,value_group_id,source_group_id);

                        double u_g_c = conf_source_value.get(dataitem).get(source_group_id).get(value_group_id);
                        double f_g_c = Math.pow(t_p_p.get(source_group_id),w_g_c*l_g_c*u_g_c);
                        double j_g_c = Math.pow(1-t_n_p.get(source_group_id),w_g_c*l_g_c*u_g_c);
                        double _f_g_c = Math.pow(1-t_p_p.get(source_group_id),w_g_c*l_g_c*u_g_c);
                        double _j_g_c = Math.pow(t_n_p.get(source_group_id),w_g_c*l_g_c*u_g_c);
                        if(f_g_c==0.0)
                            System.out.println("fgc:"+f_g_c);
                        if(f_g_cs_temp.containsKey(source_group_id)){
                            f_g_cs_temp.get(source_group_id).put(value_group_id,f_g_c);
                        }else{
                            HashMap<Integer, Double> t = new HashMap<>();
                            t.put(value_group_id, f_g_c);
                            f_g_cs_temp.put(source_group_id,t);
                        }
                        if(j_g_cs_temp.containsKey(source_group_id)){
                            j_g_cs_temp.get(source_group_id).put(value_group_id,j_g_c);
                        }else{
                            HashMap<Integer, Double> t = new HashMap<>();
                            t.put(value_group_id, j_g_c);
                            j_g_cs_temp.put(source_group_id,t);
                        }
                        if(_f_g_cs_temp.containsKey(source_group_id)){
                            _f_g_cs_temp.get(source_group_id).put(value_group_id,_f_g_c);
                        }else{
                            HashMap<Integer, Double> t = new HashMap<>();
                            t.put(value_group_id, _f_g_c);
                            _f_g_cs_temp.put(source_group_id,t);
                        }
                        if(_j_g_cs_temp.containsKey(source_group_id)){
                            _j_g_cs_temp.get(source_group_id).put(value_group_id,_j_g_c);
                        }else{
                            HashMap<Integer, Double> t = new HashMap<>();
                            t.put(value_group_id, _j_g_c);
                            _j_g_cs_temp.put(source_group_id,t);
                        }
                    }
                    double p_a_c = p_a_c_value.get(dataitem).get(value_group_id);

                    double soup_p_a_c = 1.0;
                    for(int source_group_id:value_source_map.get(dataitem).get(value_group_id)){
                        soup_p_a_c *= _f_g_cs_temp.get(source_group_id).get(value_group_id) / f_g_cs_temp.get(source_group_id).get(value_group_id);
                    }

                    double oppo_p_a_c = 1.0;
                    for(int source_group_id:source_value_map.get(dataitem).keySet()){
                        if(value_source_map.get(dataitem).get(value_group_id).contains(source_group_id))
                            continue;
                        double up = 1.0;
                        if(!_j_g_cs_temp.containsKey(source_group_id)||!_j_g_cs_temp.get(source_group_id).containsKey(value_group_id)){
                            up = 1.0;
                        }else {
                            up = _j_g_cs_temp.get(source_group_id).get(value_group_id);
                        }
                        double down = 1.0;
                        if(!j_g_cs_temp.containsKey(source_group_id)||!j_g_cs_temp.get(source_group_id).containsKey(value_group_id)) {
                            down = 1.0;
                        }else
                            down = j_g_cs_temp.get(source_group_id).get(value_group_id);
                        oppo_p_a_c *=  up/ down;
                    }

                    double p_c_x = 1.0 / (1 + ((1-p_a_c)/p_a_c)*soup_p_a_c*oppo_p_a_c);

                    if(Double.isNaN(p_c_x)){
                        System.out.println(soup_p_a_c);
                        System.out.println(oppo_p_a_c);
                        System.out.println(p_a_c);
                        System.out.println(f_g_cs_temp.toString());
                        System.out.println(_f_g_cs_temp.toString());
                        System.out.println(t_p_p.toString());
                        System.out.println(t_n_p.toString());

                        System.exit(-1);

                    }
                    veracity_score_value.get(dataitem).put(value_group_id, p_c_x);
                }
                f_g_cs.put(dataitem,f_g_cs_temp);
                j_g_cs.put(dataitem,j_g_cs_temp);
                _f_g_cs.put(dataitem,_f_g_cs_temp);
                _j_g_cs.put(dataitem,_j_g_cs_temp);
            }

            this.t_n_p_old = (HashMap<Integer,Double>)t_n_p.clone();
            this.t_p_p_old = (HashMap<Integer,Double>)t_p_p.clone();
            double t_p_p_max = 0.0;
            double t_n_p_max = 0.0;
            for(int source_group_id:source_groups.keySet()){
                double p_p_up_sum=0.0;
                double p_p_down_sum=0.0;
                for(String dataitem:dataSet.getDataItemMap().keySet()){
                    Set<Integer> c_n_g = source_value_map.get(dataitem).get(source_group_id);
                    if(c_n_g==null)
                        continue;
                    for(int value_group_id:c_n_g){
                        p_p_up_sum += veracity_score_value.get(dataitem).get(value_group_id);
                    }
                    p_p_down_sum += c_n_g.size();
                }
                if(_x_flag) {
                    t_p_p.put(source_group_id,p_p_up_sum/Math.pow(p_p_down_sum, 4.0/5));
                    if(p_p_up_sum/Math.pow(p_p_down_sum, 4.0/5) > t_p_p_max)
                        t_p_p_max  = p_p_up_sum/Math.pow(p_p_down_sum, 4.0/5);
                }else {
                    t_p_p.put(source_group_id, p_p_up_sum / p_p_down_sum);
                }

                double n_p_up_sum = 0.0;
                double n_p_down_sum = 0.0;
                for(String dataitem:dataSet.getDataItemMap().keySet()){
                    Set<Integer> c_n_g = source_value_map.get(dataitem).get(source_group_id);
                    if(c_n_g == null) {
//                        n_p_up_sum += 1;
//                        n_p_down_sum += value_groups.get(dataitem).keySet().size();
                        c_n_g = new HashSet<>();
//                        continue;
                    }
                    for(int value_group_id:value_groups.get(dataitem).keySet()){
                        if(c_n_g.contains(value_group_id))
                            continue;
                        n_p_up_sum += 1-veracity_score_value.get(dataitem).get(value_group_id);
                    }
//                    for(int value_group_id:c_n_g){
//                        n_p_up_sum += 1-veracity_score_value.get(dataitem).get(value_group_id);
//                    }
                    n_p_down_sum += value_groups.get(dataitem).keySet().size() - c_n_g.size();
                }
                if(n_p_down_sum==0)
                    t_n_p.put(source_group_id,0.0);
                else {
                    if(_x_flag) {
                        t_n_p.put(source_group_id, n_p_up_sum / Math.pow(n_p_down_sum, 4.0/5));
                        if(n_p_up_sum / Math.pow(n_p_down_sum, 4.0/5)>t_n_p_max)
                            t_n_p_max = n_p_up_sum / Math.pow(n_p_down_sum, 4.0/5);
                    }else
                        t_n_p.put(source_group_id, n_p_up_sum / n_p_down_sum);
                }
            }
            if(_x_flag) {
                for(int i:t_p_p.keySet()){
                    t_p_p.put(i, t_p_p.get(i)/(t_p_p_max+1e-11));
                }

                for(int i:t_n_p.keySet()){
                    t_n_p.put(i, t_n_p.get(i)/(t_n_p_max+1e-11));
                }
            }

            if(!copy_blind_flag){
                p_a_c_value = (HashMap<String,HashMap<Integer,Double>>)veracity_score_value.clone();
            }

            for(String dataitem:dataSet.getDataItemMap().keySet()){
                HashMap<Integer,Double> temp = veracity_score_value.get(dataitem);
                for(int value_group_id:temp.keySet()){
                    for(int value_id:value_groups.get(dataitem).get(value_group_id)){
                        dataSet.getDataItemValue(value_id).setConfidence(temp.get(value_group_id));
                    }
                }
            }

            for(String sourceid : dataSet.getSourceMap().keySet()){
                Source source = dataSet.getSourceMap().get(sourceid);
                source.setTrustworthiness(t_p_p.get(source_group_id.get(sourceid)));
                source.setSpecificity(t_n_p.get(source_group_id.get(sourceid)));
            }
            newTrustworthinessCosineSimilarity = computeCosine();
            System.out.println(newTrustworthinessCosineSimilarity);

            if (1- newTrustworthinessCosineSimilarity <= ConvergenceTester.convergenceThreshold) {
                continueComputation = false;
                last++;
            }else{
                continueComputation = true;
                last = 0;
            }
            if(!continueComputation && last>1)
                break;
        }
        return iterationCount;
    }


    private double infer_source_dependency(String dataitem, int value_group_id, int source_group_id){
        int s_g_size = source_groups.get(source_group_id).size();
        double I_g_c = 0.0;
        for(String source: source_groups.get(source_group_id)) { 
            double I_s_c = 1.0;
            for(int s_g_j:value_source_map.get(dataitem).get(value_group_id)) {
                for(String source_j:source_groups.get(s_g_j)) {
                    if (source_j.equals(source))
                        continue;
                    double p_s1_s2_c=0.0;
                    double p_a_c = p_a_c_value.get(dataitem).get(value_group_id);
                    double t_p_p_s1 = t_p_p.get(source_group_id);
                    double t_p_p_s2 = t_p_p.get(s_g_j);
                    double t_n_p_s1 = t_n_p.get(source_group_id);
                    double t_n_p_s2 = t_n_p.get(s_g_j);
                    if (copy_blind_flag) {
                        double p_sum = p_a_c * (t_p_p_s1*t_p_p_s2+(1-t_n_p_s1)*(1-t_n_p_s2))
                                + (1 - p_a_c) * (t_n_p_s1*t_n_p_s2+(1-t_p_p_s1)*(1-t_p_p_s2));
                        p_s1_s2_c = (1-2*copy_proba_blind)*p_sum/(2*copy_proba_blind+(1-2*copy_proba_blind)*p_sum);
                    }else{

                        double p_over = (1-2*copy_proba_smart_t)*p_a_c*(t_p_p_s1*t_p_p_s2+(1-t_n_p_s1)*(1-t_n_p_s2))
                                +(1-2*copy_proba_smart_f)*(1-p_a_c)*(t_n_p_s1*t_n_p_s2+(1-t_p_p_s1)*(1-t_p_p_s2));
                        p_s1_s2_c = p_over/(2*copy_proba_smart_t*p_a_c+2*copy_proba_smart_f*(1-p_a_c)+p_over);
                    }
                    I_s_c = I_s_c * ((1+p_s1_s2_c)/2.0);
                }
            }
            I_g_c += I_s_c;
        }
        return I_g_c*1.0/s_g_size;
    }

    @Override
    protected int runFusioner() {
        System.out.println(this.getClass().getSimpleName() + ">>>>");
        int cnt = run();
        return cnt;
    }


    private <E,A> HashMap<Integer, Set<E>> merge(HashMap<E,Set<A>> source_values){
        HashMap<Integer, Set<E>> source_groups = new HashMap<>();
        HashMap<E, Boolean> source_flag = new HashMap<>();
        for(E sou:source_values.keySet())
            source_flag.put(sou,true);

        int groupid = 0;
        for(E sou_i:source_values.keySet()){
            if(!source_flag.get(sou_i)){
                continue;
            }
            source_flag.put(sou_i,false);
            source_groups.put(groupid,new HashSet<E>(Arrays.asList(sou_i)));
            for(E sou_j:source_values.keySet()){
                if(!source_flag.get(sou_j)){
                    continue;
                }
                Set<A> v_i = source_values.get(sou_i);
                Set<A> v_j = source_values.get(sou_j);
                if(v_i.equals(v_j)){
                    source_groups.get(groupid).add(sou_j);
                    source_flag.put(sou_j,false);
                }
            }
            groupid++;
        }
        return source_groups;
    }


    private double computeCosine(){
        double a,b,c,d;
        double sumAB = 0;
        double sumA2=0;
        double sumB2 = 0;
        for(int source:source_groups.keySet()){
            a = t_p_p.get(source);
            b = t_p_p_old.get(source);
            c = t_n_p.get(source);
            d = t_n_p_old.get(source);
            sumAB+=a*b+c*d;
            sumA2+=a*a+c*c;
            sumB2+=b*b+d*d;
        }
        sumA2 = Math.pow(sumA2, 0.5);
        sumB2 = Math.pow(sumB2, 0.5);
        if ((sumA2 * sumB2) == 0) {
            return Double.MAX_VALUE;
        }
        if (Double.isInfinite(sumAB)) {
            if (Double.isInfinite((sumA2 * sumB2))) {
                return 1.0;
            }
        }
        return sumAB/(sumA2*sumB2);
    }
}
