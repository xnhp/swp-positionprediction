package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.location.Location;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Debug;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Location2D;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;

public class AlgorithmSimilarTrajectory implements SingleTrajPredictionAlgorithm {

    private Context context;

    public AlgorithmSimilarTrajectory ( Context context ) {
        this.context = context;
    }


    @Override
    public LinkedList<Location3D> predict(Date date_past, Date date_pred, int study_id, int bird_id) {

        // Hardcoded
        double loc_long[] = {
                -73.8987953, -73.8987189, -73.8986926, -73.8986236, -73.8987095, -73.8988455, -73.8986833, -73.8986874, -73.8987146, -73.8987253, -73.8986618, -73.8986659, -73.8987226, -73.8986956, -73.8988062, -73.8986425, -73.8987239, -73.8987597, -73.8987056, -73.8987516, -73.8985559, -73.8986395, -73.8986293, -73.8987432, -73.8987132, -73.8987689, -73.8987488, -73.8987559, -73.8987575, -73.8987135, -73.8987508, -73.8985863, -73.8988222, -73.8987608, -73.898765, -73.8987875, -73.8985889, -73.8986298, -73.8988075, -73.8986946, -73.8987254, -73.8987774, -73.8987576, -73.8988706, -73.8987488, -73.8987186, -73.8987178, -73.898673, -73.8987521, -73.8986535, -73.8987575, -73.8987731, -73.8987009, -73.8987385, -73.8987529, -73.8989285, -73.8947159, -73.8923453, -73.8900319, -73.8816776, -73.8765794, -73.8758821, -73.8765932, -73.8765832, -73.8763189, -73.8764523, -73.8765803, -73.8750774, -73.8739461, -73.8739882, -73.871696, -73.8717901, -73.8724412, -73.8677151, -73.8676569, -73.8666138, -73.8627917, -73.8592644, -73.8528536, -73.8480161, -73.8482094, -73.8482716, -73.8483249, -73.848296, -73.848351, -73.8477049, -73.8486686, -73.8485784, -73.8483287, -73.8471768, -73.843708, -73.8436889, -73.8452219, -73.8466947, -73.8469191, -73.8464949, -73.8464981, -73.8462396, -73.846509, -73.8465198, -73.8465147, -73.8465175, -73.8464655, -73.8464425, -73.8464957, -73.8465008, -73.8465118, -73.8464616, -73.8462986, -73.8461033, -73.8464007, -73.8465188, -73.8467521, -73.8465678, -73.8466746, -73.8443165, -73.8449545, -73.8449012, -73.8448812, -73.8486415, -73.8546723, -73.8559318, -73.856013, -73.85481, -73.8600766, -73.8628471, -73.8644906, -73.8673027, -73.8721472, -73.8753837, -73.8753375, -73.8755271, -73.8744583, -73.8743245, -73.8742482, -73.873712, -73.8739753, -73.873467, -73.873971, -73.8743524, -73.8736289, -73.8736962, -73.8825919, -73.8912606, -73.8919492, -73.8936564, -73.8934818, -73.896941, -73.9011365, -73.902349, -73.9053629, -73.9119316, -73.9155785, -73.9187023, -73.9232526, -73.9254667, -73.9104822, -73.9140892, -73.9145783, -73.9145528, -73.9145889, -73.9145911, -73.9138824, -73.913966, -73.9139996, -73.9139793, -73.9138778, -73.9139499, -73.9139511, -73.9139993, -73.9139044, -73.9137916, -73.9139833, -73.9149528, -73.9139829, -73.9139325, -73.9138598, -73.9138877, -73.9139702, -73.9138786, -73.913892, -73.9149802, -73.9120995, -73.9150548, -73.9146008, -73.9145622, -73.914627, -73.9149936, -73.9145376, -73.9146304, -73.914674, -73.9137928, -73.9136599, -73.9141989, -73.9135779, -73.9138265, -73.9138262, -73.9137644, -73.9136992, -73.9136817, -73.9138779, -73.910327, -73.9063369, -73.9064236, -73.9065749, -73.9064775, -73.906865, -73.9064373, -73.9043729, -73.9015716, -73.8972548, -73.894753, -73.8938446, -73.8939046, -73.8938021, -73.8940741, -73.8941008, -73.8939494, -73.8941131, -73.8941805, -73.894165, -73.8941418, -73.8941477, -73.8935739, -73.8892968, -73.887196, -73.8907808, -73.891485, -73.891502, -73.8916152, -73.8918278, -73.8943372, -73.8942991, -73.8932611, -73.8947585, -73.9002414, -73.9038008, -73.9040715, -73.9041057, -73.9039524, -73.903892, -73.9039952, -73.9005972, -73.8963081, -73.8949809, -73.8956698, -73.895476, -73.8951608, -73.8943661, -73.8939724, -73.8940266, -73.8940086, -73.8940624, -73.8940076, -73.8940383, -73.8940465, -73.8940969, -73.8939732, -73.8940509, -73.8943524, -73.8939038, -73.8940563, -73.8940948, -73.89401, -73.8939831, -73.8939943, -73.8940465, -73.8941085, -73.8940807, -73.894356, -73.8940623, -73.8939826, -73.8936908, -73.8940442, -73.8939219, -73.8939699, -73.8939309, -73.8940543, -73.8940396, -73.8939133, -73.8943881, -73.8935673, -73.8937686, -73.8940869, -73.8940502, -73.8940245, -73.8933224, -73.8939967, -73.8940283, -73.8940434, -73.8942356, -73.894006, -73.8940427, -73.8933393, -73.8904661, -73.8755566, -73.8746832, -73.8712447, -73.8718469, -73.87401, -73.8838679, -73.8927762, -73.8927543, -73.892571, -73.8924855, -73.8925521, -73.8924538, -73.894417, -73.8998829, -73.905067, -73.9156949, -73.9157106, -73.9157453, -73.9091709, -73.9036735, -73.9030709, -73.8970354, -73.890279, -73.886369, -73.8885454, -73.885518, -73.8876273, -73.8838428, -73.8787681, -73.8745263, -73.8744461, -73.8715198, -73.8713441, -73.8709601, -73.8672166, -73.867152, -73.8671821, -73.8673232, -73.8717657, -73.8675248, -73.8674967, -73.8648485, -73.8679998, -73.8618958, -73.8562263, -73.8493596, -73.8468786, -73.8469595, -73.8480746, -73.8531413, -73.8536475, -73.8596369, -73.8658161, -73.8679729, -73.8678477, -73.8678646, -73.867858, -73.8678279, -73.8678043, -73.8678081, -73.8703838, -73.8684207, -73.8670469, -73.8655066, -73.8716584, -73.8711492, -73.8741559, -73.8741389, -73.8734407, -73.8728621, -73.8808196, -73.8810716, -73.8730035, -73.8689551, -73.8635675, -73.8592955, -73.8612758, -73.8624768, -73.8703367, -73.8778735, -73.8812322, -73.8772618, -73.8870004, -73.8923643, -73.8948035, -73.8936689, -73.8948121, -73.9002866, -73.916528, -73.9159909, -73.9162823, -73.9165291, -73.9164103, -73.9166185, -73.9164531, -73.9164952, -73.9165632, -73.9164245, -73.9145217, -73.9094611, -73.904643, -73.9000746, -73.8944328, -73.892078, -73.8883838, -73.8868167, -73.8811776, -73.877215, -73.8745427, -73.8610872, -73.8582574, -73.8508243, -73.845058, -73.8437575, -73.8437044, -73.8437205, -73.8436635, -73.8451436, -73.8456779, -73.8479642, -73.8474336, -73.8466838, -73.8505126, -73.8567319, -73.8599427, -73.8625401, -73.8654408, -73.8716305, -73.872796, -73.8748143, -73.8772527, -73.8770002, -73.8772181, -73.8771029, -73.8776744, -73.8839361, -73.8892456, -73.8951551, -73.9023936, -73.9145394, -73.9145692, -73.919165, -73.9216665, -73.9274661, -73.9232417, -73.9232334, -73.9232298, -73.9231781, -73.9232075, -73.923422, -73.918615, -73.9144468, -73.9027312, -73.8972547, -73.8973161, -73.8935494, -73.8939205, -73.893506, -73.8942706, -73.8949845, -73.8937288, -73.8857629, -73.8771083, -73.8742201, -73.8742642, -73.8741606, -73.8741354, -73.8742183, -73.8680976, -73.867561, -73.8579147, -73.8637689, -73.8614698, -73.8525273, -73.8462594, -73.8484562, -73.8436605, -73.8436724, -73.8437375, -73.8437167, -73.8446981, -73.8451775, -73.8451225, -73.8452628, -73.8452141, -73.8451416, -73.8451083, -73.8449348, -73.8454246, -73.8451996, -73.8451689, -73.845104, -73.8452683, -73.8449605, -73.8450523, -73.8578171, -73.8580196, -73.8592581, -73.8555871, -73.8597373, -73.8623696, -73.8634244, -73.8656892, -73.8675083, -73.8718767, -73.8730603, -73.8755731, -73.8754332, -73.8795363, -73.8853875, -73.8946666, -73.895142, -73.892512, -73.8898177, -73.8888068, -73.8889204, -73.8888052, -73.8888716, -73.8887535, -73.8890321, -73.8887509, -73.8887132, -73.8887774, -73.8886661, -73.8890214, -73.8887784, -73.888799, -73.8887621, -73.8887644, -73.8887965, -73.8887993, -73.8888211, -73.8888186, -73.8887376, -73.8887825, -73.8888253, -73.8887515, -73.8887564, -73.8887562, -73.8889109, -73.8887725, -73.8887255, -73.8887835, -73.888625, -73.8886878, -73.8887919, -73.8887827, -73.8887913, -73.8887531, -73.8887744, -73.888702, -73.8887158, -73.8887538, -73.8889558, -73.8888004, -73.8887776, -73.888668, -73.8887871, -73.8888171, -73.888816, -73.8887668, -73.8887904, -73.8886713, -73.8885945, -73.8945915, -73.8993125, -73.9047494, -73.9078117, -73.9131004, -73.914511, -73.9145071, -73.9145059, -73.9145375, -73.9145383, -73.9139853, -73.9136938, -73.913558, -73.9139171, -73.9136785, -73.9137189, -73.9136901, -73.9137026, -73.9136814, -73.9136483, -73.9138173, -73.9138164, -73.9137136, -73.9138241, -73.9138079, -73.9138437, -73.913828, -73.9135937, -73.9139002, -73.9142018, -73.9138516, -73.9133586, -73.9133673, -73.9138161, -73.9135382, -73.9177654, -73.9199259, -73.9264526, -73.9281395, -73.9302492, -73.9306653, -73.9300163, -73.9156621, -73.9157847, -73.9157433, -73.9130436, -73.908677, -73.9019679, -73.9018671, -73.8962482, -73.8919997, -73.8839916, -73.8771917, -73.8772353, -73.8772704, -73.8772418, -73.877225, -73.8771949, -73.877259, -73.877249, -73.877275, -73.8772943, -73.8772604, -73.8772283, -73.8770995, -73.8748326, -73.8747374, -73.8742063, -73.8742583, -73.8740411, -73.8741296, -73.8741067, -73.8741678, -73.8742492, -73.873034, -73.8710597, -73.8716391, -73.8728524, -73.8719012, -73.8760146, -73.8775566, -73.8805624, -73.8854533, -73.8915632, -73.89356, -73.8938522, -73.8935757, -73.8935702, -73.8934961, -73.8936186, -73.8936832, -73.8936065, -73.8936929, -73.8935893, -73.8935139, -73.8935818, -73.8936836, -73.893737, -73.8937037, -73.8935406, -73.8934666, -73.8933182, -73.8937503, -73.8938899, -73.8933887, -73.8935501, -73.8934306, -73.8936611, -73.8935284, -73.8935032, -73.8934866, -73.8934825, -73.8935304, -73.8934368, -73.8934417, -73.8935075, -73.8934508, -73.8934005, -73.8934697, -73.8934762, -73.8934565, -73.8934531, -73.8936668, -73.8970665, -73.897654, -73.8978157, -73.8945057, -73.8929846, -73.8940255, -73.8927971, -73.8906663, -73.8878259, -73.8813927, -73.8813691, -73.8813911, -73.8808994, -73.8815455, -73.8820519, -73.8810509, -73.8815243, -73.8814856, -73.8814292, -73.8814582, -73.881307, -73.8812958, -73.88169, -73.8816134, -73.8736547, -73.8748121, -73.8739969, -73.8702738, -73.867496, -73.8676039, -73.8621213, -73.8574096, -73.8499724, -73.8511839, -73.8505734, -73.8500904, -73.8456123, -73.8451872, -73.8450411, -73.8451344, -73.8451954, -73.8451265, -73.8453459, -73.8452429, -73.8450951, -73.8451645, -73.8451973, -73.8448915, -73.845148, -73.845023, -73.8451163, -73.8451433, -73.8452263, -73.8481575, -73.850911, -73.8480234, -73.8452161, -73.8452061, -73.845271, -73.8452329, -73.8453522, -73.8454073, -73.845298, -73.8460325, -73.8452422, -73.8455512, -73.8498339, -73.8538675, -73.8874084, -73.8943102, -73.8973658, -73.8934495, -73.8943066, -73.8991683, -73.900729, -73.8984491, -73.8985551, -73.9025523, -73.8993782, -73.8969657, -73.8969828, -73.8971281, -73.8969241, -73.8970002, -73.8969203, -73.8968277, -73.8969706, -73.896941, -73.8970882, -73.8970132, -73.8970685, -73.8970537, -73.8930125, -73.8938739, -73.8959447, -73.8967371, -73.8959836, -73.896161, -73.8960725, -73.8974656, -73.9008687, -73.9040959, -73.9042118, -73.9041607, -73.9042159, -73.904289, -73.9042064, -73.9041336, -73.9041529, -73.9041533, -73.9041779, -73.9041487, -73.9045052, -73.9040707, -73.9042947, -73.9040468, -73.905285, -73.9041637, -73.9043813, -73.9042588, -73.9042809, -73.9043328, -73.9041892, -73.903982, -73.9040943, -73.9041652, -73.9042603, -73.9042056, -73.9042233, -73.9042939, -73.9042249, -73.9043628, -73.9042144, -73.9042998, -73.91148, -73.9132515, -73.9096754, -73.9094756, -73.9093432, -73.909393, -73.9094129, -73.9093147, -73.9094131, -73.9095376, -73.9094457, -73.9094215, -73.9093918, -73.9094022, -73.9094645, -73.9093893, -73.9093527, -73.9065311, -73.8993706, -73.8945548, -73.8861364, -73.8826546, -73.8783472, -73.8768376, -73.8756917, -73.8759449, -73.875571, -73.8758011, -73.8757067, -73.8755833, -73.8755476, -73.8755568, -73.8756828, -73.8756819, -73.8756535, -73.8756044, -73.8755053, -73.8758131, -73.875609, -73.8744435, -73.8723004, -73.8719748, -73.8724688, -73.8720421, -73.8718769, -73.876052, -73.8741312, -73.8754554, -73.8731583, -73.8663507, -73.8653429, -73.8656599, -73.8656744, -73.8655506, -73.8655883, -73.8655343, -73.8654601, -73.8654992, -73.8655247, -73.8655881, -73.865594, -73.8655525, -73.8655588, -73.8655734, -73.8653169, -73.8655294, -73.8655445, -73.8654201, -73.8654442, -73.8654944, -73.8655239, -73.8654416, -73.865563, -73.8654878, -73.8655123, -73.8654849, -73.8654597, -73.8654999, -73.8655022, -73.8654629, -73.8655656, -73.8654352, -73.8663719, -73.8654937, -73.8653725, -73.865518, -73.8655034, -73.865514, -73.8655533, -73.865468, -73.8653745, -73.8656792, -73.8654343, -73.8654801, -73.8754589, -73.8752631, -73.8750011, -73.8811669, -73.882439, -73.879783, -73.8742365, -73.870114, -73.873537
        };

        double loc_lat[] = {
                42.7437001, 42.7436887, 42.7436421, 42.7437442, 42.7436847, 42.7436514, 42.7436892, 42.7435338, 42.7436261, 42.7436378, 42.7436866, 42.7436979, 42.743475, 42.7437103, 42.7438171, 42.7439238, 42.7437107, 42.7436946, 42.7437558, 42.7437183, 42.7434417, 42.7437042, 42.743704, 42.743721, 42.7436465, 42.7436541, 42.7436571, 42.7437368, 42.7436218, 42.7435118, 42.7437367, 42.7437001, 42.7434356, 42.7436549, 42.7436324, 42.7437024, 42.7436303, 42.7435734, 42.7437131, 42.7436766, 42.7436286, 42.7436839, 42.7437133, 42.7435253, 42.7436178, 42.7436109, 42.7436772, 42.7436818, 42.743657, 42.7436444, 42.7434437, 42.7436074, 42.7436059, 42.7436193, 42.7436501, 42.7436559, 42.7452779, 42.7451711, 42.7450136, 42.7471881, 42.7485498, 42.7497628, 42.7483238, 42.7485848, 42.7487628, 42.7486926, 42.7486375, 42.7460537, 42.7416695, 42.7400448, 42.7402133, 42.7383221, 42.7350889, 42.7313962, 42.7311668, 42.7277694, 42.7245673, 42.7187798, 42.7188156, 42.7180057, 42.7181548, 42.718164, 42.717982, 42.7181428, 42.7181611, 42.7183173, 42.7181224, 42.7180181, 42.7179878, 42.7170221, 42.7195555, 42.7195722, 42.7215967, 42.723838, 42.7240001, 42.7235834, 42.7236729, 42.7236289, 42.7238076, 42.7237059, 42.7238883, 42.7238233, 42.7237802, 42.7238884, 42.7237832, 42.7237718, 42.7237569, 42.723736, 42.7238116, 42.7239482, 42.7236262, 42.7237474, 42.724001, 42.7237858, 42.7239254, 42.7225839, 42.7235839, 42.7235437, 42.723425, 42.7184737, 42.7146073, 42.7132339, 42.7132193, 42.7182522, 42.72008, 42.7240013, 42.7272256, 42.7282776, 42.7300801, 42.7347389, 42.7347619, 42.737676, 42.7401418, 42.7399342, 42.7397329, 42.7400366, 42.7399092, 42.7401536, 42.7397835, 42.7395846, 42.7401206, 42.7398277, 42.7467144, 42.751569, 42.752686, 42.7509754, 42.7503655, 42.7518038, 42.7541476, 42.7531138, 42.7557714, 42.7586791, 42.7561085, 42.7580651, 42.7605827, 42.7613464, 42.7529172, 42.7479491, 42.7458839, 42.7459227, 42.7458684, 42.7459041, 42.7447679, 42.744682, 42.7446759, 42.7447162, 42.7447877, 42.7447921, 42.744871, 42.7451034, 42.7448582, 42.7448162, 42.7447706, 42.7447333, 42.7447519, 42.744748, 42.7447534, 42.7447527, 42.7447187, 42.7447047, 42.7447733, 42.7446634, 42.7441147, 42.7442783, 42.745798, 42.7459009, 42.745836, 42.7455798, 42.745873, 42.7456896, 42.7458023, 42.7453523, 42.7447428, 42.7449846, 42.7448321, 42.7445597, 42.7448312, 42.7448321, 42.7446179, 42.744823, 42.7447939, 42.7422645, 42.7414796, 42.7427867, 42.7428576, 42.7427443, 42.7428973, 42.742765, 42.7449928, 42.7508357, 42.7524891, 42.7565788, 42.7578333, 42.7576043, 42.7577544, 42.7576737, 42.7576131, 42.7577432, 42.757688, 42.757721, 42.7576927, 42.7577074, 42.7577161, 42.7563938, 42.7538691, 42.7546285, 42.7526809, 42.7530854, 42.7531501, 42.7531173, 42.7530432, 42.757527, 42.7579801, 42.7578064, 42.7607244, 42.7635385, 42.7625637, 42.7625322, 42.7627493, 42.7624489, 42.762599, 42.7625805, 42.7651648, 42.7687003, 42.7652181, 42.7587584, 42.7586767, 42.7577966, 42.7569872, 42.7563579, 42.7563387, 42.7562944, 42.7563788, 42.7562805, 42.7563782, 42.7563889, 42.7564457, 42.756313, 42.7562666, 42.7564407, 42.7563519, 42.7563551, 42.7562265, 42.7563973, 42.7563424, 42.7563514, 42.7561176, 42.7565033, 42.7559901, 42.756332, 42.7563218, 42.7562198, 42.7565824, 42.7562936, 42.7565221, 42.756296, 42.7563766, 42.7561553, 42.7561561, 42.7562267, 42.7561979, 42.7558937, 42.7561709, 42.7562342, 42.7564051, 42.7563709, 42.7569148, 42.7562475, 42.7563408, 42.7563053, 42.7560662, 42.7562437, 42.7562914, 42.7583722, 42.7547706, 42.7493729, 42.74648, 42.7443618, 42.7402541, 42.7379648, 42.7377007, 42.7462188, 42.746127, 42.746129, 42.7461371, 42.7461323, 42.7461425, 42.7475315, 42.7442426, 42.7386024, 42.7446796, 42.7446307, 42.7446602, 42.746135, 42.7454617, 42.7390962, 42.7333114, 42.7276803, 42.7282011, 42.7289052, 42.7339458, 42.7346019, 42.7343718, 42.7357154, 42.7398755, 42.7398822, 42.7382572, 42.7405036, 42.7431742, 42.7408696, 42.740822, 42.7408579, 42.740688, 42.7407135, 42.7354725, 42.7311849, 42.7305475, 42.7287462, 42.7232222, 42.7178895, 42.7165216, 42.7170973, 42.7171086, 42.7188113, 42.7165407, 42.7145543, 42.7125745, 42.7138016, 42.7166716, 42.7169721, 42.7169159, 42.7169699, 42.7169254, 42.7169165, 42.7168786, 42.7220029, 42.7275962, 42.7307633, 42.7339692, 42.7357332, 42.7394896, 42.7410191, 42.740992, 42.7416264, 42.7475676, 42.7471383, 42.7457999, 42.7454038, 42.7418702, 42.7394944, 42.7359596, 42.7351297, 42.7343277, 42.7387143, 42.7426591, 42.7456685, 42.7494639, 42.746001, 42.7431083, 42.7421617, 42.7419404, 42.7422332, 42.7374265, 42.7463387, 42.7458061, 42.7455771, 42.7453552, 42.7452859, 42.7453808, 42.7456585, 42.745387, 42.7451227, 42.7453197, 42.7460244, 42.7593159, 42.763102, 42.7633798, 42.7581336, 42.7553165, 42.7533909, 42.7515412, 42.7501333, 42.7513306, 42.7463911, 42.721159, 42.7202273, 42.7187321, 42.720781, 42.7196393, 42.7195557, 42.7195171, 42.7196496, 42.7229929, 42.7236831, 42.7177108, 42.7140293, 42.7089802, 42.710864, 42.7164981, 42.7181531, 42.723597, 42.7273649, 42.7296714, 42.7308239, 42.7340235, 42.7335715, 42.733627, 42.7334674, 42.7336434, 42.7291942, 42.7286587, 42.7282854, 42.7315563, 42.7366214, 42.7459289, 42.7459191, 42.7473411, 42.7525499, 42.7534319, 42.760537, 42.7605025, 42.7604885, 42.7605222, 42.760478, 42.7604802, 42.7587779, 42.7562978, 42.7515505, 42.7523982, 42.7520798, 42.7518484, 42.7509559, 42.750932, 42.7507725, 42.7498648, 42.747136, 42.7449837, 42.7374045, 42.7413851, 42.7410375, 42.7410588, 42.7410617, 42.7410489, 42.7417509, 42.7359999, 42.730182, 42.7292036, 42.7224226, 42.7191121, 42.7168325, 42.7169494, 42.7195437, 42.7195046, 42.7195925, 42.7195812, 42.7232669, 42.7237008, 42.7237358, 42.7236046, 42.7237722, 42.7236854, 42.7236138, 42.7237361, 42.7236286, 42.7237348, 42.7236836, 42.7232429, 42.7236348, 42.7238441, 42.7236914, 42.7122744, 42.7122069, 42.7120593, 42.7174757, 42.7199355, 42.7227664, 42.7260816, 42.73107, 42.732116, 42.7363223, 42.7403899, 42.7466937, 42.7526061, 42.7568438, 42.7582534, 42.7575288, 42.7575761, 42.7530232, 42.7527676, 42.7533936, 42.7533189, 42.7533833, 42.753507, 42.7533758, 42.7533843, 42.7533018, 42.7532568, 42.7533269, 42.7533295, 42.7532185, 42.7533593, 42.7533501, 42.7533928, 42.7533978, 42.7533953, 42.7533683, 42.753468, 42.7534186, 42.7534464, 42.7532614, 42.753444, 42.7533492, 42.7533545, 42.7534436, 42.7531361, 42.7533142, 42.7531805, 42.7533102, 42.7532691, 42.7533093, 42.7533545, 42.753359, 42.7533894, 42.7534332, 42.7533036, 42.7531657, 42.7532537, 42.7534067, 42.7535742, 42.7533793, 42.7533567, 42.753442, 42.7533667, 42.7533461, 42.7533247, 42.7533755, 42.7533453, 42.7533984, 42.7533951, 42.7523773, 42.7508438, 42.7456013, 42.7424703, 42.7422532, 42.7458819, 42.7458428, 42.7458663, 42.7457962, 42.7457701, 42.7444807, 42.7446626, 42.7448137, 42.7447981, 42.7447133, 42.744648, 42.7447395, 42.7446863, 42.7447055, 42.7448426, 42.7446445, 42.7448135, 42.7446677, 42.7446573, 42.7446771, 42.7446643, 42.7447116, 42.744735, 42.7445861, 42.7445772, 42.7447624, 42.7447537, 42.7445959, 42.744659, 42.7446908, 42.7464037, 42.7489897, 42.7527007, 42.7547415, 42.7540034, 42.753419, 42.7569307, 42.7552851, 42.7551651, 42.7552829, 42.7543973, 42.7501876, 42.7475619, 42.7443917, 42.747294, 42.7455911, 42.7450135, 42.741576, 42.7415555, 42.7415235, 42.7415565, 42.7416397, 42.7416505, 42.7415644, 42.7415953, 42.7415071, 42.7416251, 42.7416485, 42.7415095, 42.7404332, 42.7398708, 42.7396689, 42.7413859, 42.741413, 42.7413811, 42.741402, 42.7414367, 42.7414509, 42.7415956, 42.738438, 42.7368603, 42.7381399, 42.7378505, 42.7376266, 42.7357623, 42.7335157, 42.7349525, 42.7381549, 42.7394919, 42.7422817, 42.7418988, 42.74242, 42.7424711, 42.7421715, 42.7421948, 42.7424232, 42.7424096, 42.7424534, 42.7424505, 42.7424023, 42.7424665, 42.7423955, 42.7423915, 42.7424519, 42.7421709, 42.7419735, 42.7416915, 42.7420872, 42.742064, 42.7418127, 42.7418057, 42.741844, 42.7417979, 42.7413657, 42.7419778, 42.7418761, 42.7419327, 42.7419324, 42.7419154, 42.7418771, 42.7419211, 42.7419213, 42.742181, 42.7420864, 42.7418202, 42.7419629, 42.7419733, 42.7424333, 42.7406225, 42.740605, 42.7405544, 42.7418353, 42.7424389, 42.7428036, 42.7426591, 42.7432035, 42.7451055, 42.7462678, 42.7463519, 42.7463156, 42.7468002, 42.7462408, 42.7456217, 42.7467147, 42.7463527, 42.7463673, 42.7463924, 42.7463767, 42.7463216, 42.7463189, 42.7461133, 42.7463805, 42.7484033, 42.7464485, 42.7428696, 42.7384906, 42.7324477, 42.7295923, 42.7226537, 42.7182499, 42.7189006, 42.7220437, 42.7235629, 42.7242251, 42.7213863, 42.7236921, 42.7240057, 42.7238337, 42.7237162, 42.7237084, 42.7237263, 42.7236172, 42.7236147, 42.7237427, 42.7238118, 42.7233482, 42.7235454, 42.7237344, 42.7238233, 42.7237019, 42.7236026, 42.7181444, 42.7127563, 42.7100408, 42.7240354, 42.7237286, 42.7238799, 42.7237693, 42.7236368, 42.7240823, 42.723718, 42.72409, 42.7237544, 42.7215515, 42.7208473, 42.7192327, 42.7289026, 42.730687, 42.7350422, 42.7336724, 42.7335604, 42.7374869, 42.737336, 42.7452782, 42.7466745, 42.7522983, 42.753218, 42.7535794, 42.7536066, 42.7536906, 42.7535767, 42.7536142, 42.7540024, 42.7535832, 42.7536781, 42.7536387, 42.7535765, 42.7536033, 42.7536169, 42.7536536, 42.755288, 42.7577417, 42.7592226, 42.7634959, 42.7642334, 42.7645268, 42.7643832, 42.7639842, 42.7630788, 42.7632086, 42.7633066, 42.7632724, 42.763274, 42.7632795, 42.7632731, 42.7632997, 42.7632826, 42.7632796, 42.7633218, 42.7633743, 42.7635736, 42.7633037, 42.7631528, 42.7632186, 42.764054, 42.7634404, 42.7632844, 42.7637151, 42.7636252, 42.763683, 42.7634554, 42.7632375, 42.7633013, 42.7633414, 42.7632143, 42.7632455, 42.7632457, 42.7631817, 42.7632657, 42.7631836, 42.7634238, 42.7632017, 42.7634618, 42.7639311, 42.7612127, 42.7593533, 42.7592253, 42.7593193, 42.7593211, 42.7593773, 42.7594758, 42.7595124, 42.7593371, 42.7594455, 42.7593708, 42.7592708, 42.7592939, 42.7593303, 42.7572165, 42.7561147, 42.7523561, 42.7521479, 42.7510642, 42.7515006, 42.7501768, 42.7467447, 42.7434608, 42.7432442, 42.7433396, 42.7435082, 42.7433897, 42.7433983, 42.7434401, 42.7434128, 42.7433406, 42.7433048, 42.7433649, 42.743383, 42.7431768, 42.744143, 42.7433667, 42.7414518, 42.739207, 42.7387841, 42.737761, 42.7388388, 42.7367647, 42.7357907, 42.7383702, 42.7386615, 42.7385795, 42.73255, 42.7300176, 42.7301062, 42.7301077, 42.7300379, 42.7301195, 42.7300093, 42.7300783, 42.7300835, 42.7300212, 42.7300274, 42.7300857, 42.7300677, 42.7299388, 42.7300494, 42.7300968, 42.7300073, 42.7300287, 42.7299232, 42.7299967, 42.7300036, 42.7300389, 42.7299694, 42.7300694, 42.7300794, 42.7300166, 42.7299882, 42.7300196, 42.7300327, 42.7299841, 42.7300486, 42.7299911, 42.7300765, 42.730019, 42.7300969, 42.7300309, 42.730067, 42.7300804, 42.7300451, 42.7300535, 42.7300653, 42.7299869, 42.7300712, 42.730031, 42.7299263, 42.7388753, 42.7394717, 42.7394121, 42.7378817, 42.7358701, 42.7339667, 42.7410946, 42.7400536, 42.7400224
        };

        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();

        // Length of trajectory
        int traj_length = 5;

        // Make data
        Location3D data[] = new Location3D[loc_long.length];
        for (int i = 0; i<loc_long.length; i++) {
            data[i] = new Location3D(loc_long[i], loc_lat[i], 0);
        }

        int number_of_comp = 5; // Use last 5 data points




        // Algorithm
        int size = data.length;

        double eps = 1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        List<Number> angles = new LinkedList<Number>();
        for (int j = 1; j <= traj_length; j++) {
            Location3D curr_loc = data[size-j].getLocation().to3D();
            Location3D pre_loc  = data[size-j-1].getLocation().to3D();
            double alpha = curr_loc.getAngle(pre_loc);
            angles.add(alpha);
        }

        // Find possible other trajectories and add the index of the last point to list
        List<Number> possible_indices = new LinkedList<Number>();

        // Run through all locations (or endpoint of all trajectories)
        for (int i = traj_length + 1;  i < size; i++) {
            System.out.println("LOCATION: " + i);

            // Check if angle is approx. the same
            boolean is_similar = false;

            // Run backwards through every trajectory
            for (int k = 1; k < traj_length; k++) {
                is_similar = false;
                System.out.println("traj: " + k);
                // Get angle of two locations
                Location3D curr_loc = data[i-k].getLocation().to3D();
                Location3D pre_loc  = data[i-k-1].getLocation().to3D();
                double alpha = curr_loc.getAngle(pre_loc);

                if ( Math.abs( alpha - (double) angles.get(k-1) ) < eps ) {
                    is_similar = true;
                    System.out.println("Similar");
                }
                if (!is_similar) {
                    System.out.println("Break");
                    break; // The trajectory is probably not similar, so we don't check the other locations of the trajectory
                }
            }

            // If all locations are similar, we can add the last point of the trajectory to our list
            if (is_similar) {
                possible_indices.add(i);
            }




        }

        Debug d = new Debug();
        d.print(possible_indices, "Indices");


        // TODO What happens with the vectors

        return null;
    }
}
