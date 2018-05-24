package project.software.uni.positionprediction.classes;

import java.util.Arrays;
import java.util.Date;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;

public class PredictionImplementation implements PredictionAlgorithm {

    /**
     * Hard-coded implementation of an prediction algorithm to use it for visualization
     */
    @Override
    public LocProbTupel[][] predict(Date date_past, Date date_pred, int bird_id) {

        // ALTERN.: Get data from database query
        // From leroy dataset
        double distance[] = {
                6.38246144849837,5.60672166375179,12.6712631238668,9.6518011493245,11.7339211258739,13.9288183181857,17.2662990453109,10.492484138161,1.5674350398066,7.51139776240618,1.29940528392222,25.1929171335832,26.2322673742124,14.9253592637342,17.8927549576098,24.5931468816675,3.43382056657077,8.11433315683786,5.61603423591706,34.6540259459578,29.9531649094964,0.835461871647578,9.51530956868324,8.63287632557812,4.63815424656265,1.67917375191149,8.87275840125249,12.7757622770482,12.7396717293709,25.1696130461477,14.0693828769441,35.1628327008879,24.8748735626355,2.5230206846072,7.99139416688965,18.126699708944,7.15321881114008,21.2729478295335,10.0942988880306,5.89850367832564,7.47438630652466,3.646221417406,22.842243121985,14.3194564997053,2.58882769351324,7.36540866565438,3.70360545501539,7.03823288607199,8.19372078410713,23.8661606080461,18.2298673286554,5.91402526400446,3.41964729084332,3.61895858309744,14.392403783641,389.147602685111,194.460013446441,190.221143874134,725.416529707837,443.972331407778,146.344213547267,170.127096341094,29.0054827504492,29.3127780345827,13.4201303469869,12.1362960392636,312.292313663098,495.761367172595,180.517087090941,188.625017629821,210.230235388874,363.104296034769,563.976720610612,25.9253763235692,386.95619226504,473.840261621383,704.850897304381,525.135395740217,406.336457413103,22.9138349296284,5.19645904402839,20.6839103733375,18.0190087180514,4.94261774632719,55.6956422476933,81.8544597972735,13.7417276471178,20.7287513268319,142.868587280486,399.920499774124,2.42677955656954,257.576089172635,276.664377298808,25.7306732799406,57.8786113236423,9.94577223772382,21.7294312319747,29.6808371642511,11.332161491466,20.2666391644868,7.22431928515108,6.40806931763482,12.1663804452241,12.4723000581111,1.33350886768896,1.88452065453223,4.72186941180035,15.7723589456238,22.0486018756658,43.2763763354156,16.5783300301056,34.0408784147359,28.2729427865036,17.8047742835976,243.950944050143,122.764420056817,6.24505789219652,13.2874308603057,630.392488780977,654.617746354427,184.179303171712,6.84670597825211,567.709045461923,476.789696901735,491.170156319161,382.637415815642,258.264214635271,444.420061219972,581.457951911197,4.56529145225848,324.092486822831,287.561676836561,25.531979791166,23.2183381197062,55.3711620869866,25.7901324814115,49.6937544362308,58.253732230075,38.2565020600819,83.9947645993148,33.0009525873079,1056.31444016807,891.36818477981,136.290848976446,235.891269084643,69.2440123474411,325.165403236282,431.004316789898,151.795572185084,384.754742127313,627.280548405861,413.124304444576,335.611683449612,465.789821535909,200.111816249391,1543.20709991642,625.934792936813,232.887240025394,4.78925486134163,6.71730376074309,3.96992257523248,138.917249825587,11.7435876929747,2.83332029556333,4.77542731619461,11.4958032772872,5.9235777627047,8.76537546526829,26.1166961497981,28.3253130951697,10.3473879972762,16.4931162281895,79.4884823644064,79.4400497091131,4.14931524695016,5.98265775598754,2.28571036633846,7.73914867670421,7.65955230477578,7.69919617857718,89.9318680624865,243.615136911106,242.656801018959,172.864110293149,11.8597852332674,8.95139412717488,41.363811680612,49.5460980922921,21.7442168420117,13.0185927945228,87.7754117840146,68.576815496035,51.6637888272509,53.5938456361158,36.4692260179864,30.1603378874358,5.06102177169561,24.38648780697,22.8291112884123,16.3864138040816,404.333413962033,338.150012157564,145.376187900531,14.6801782040664,14.9002087154769,35.9941873897935,37.9790863849346,299.697094643324,688.404610041959,398.289493046912,498.347549716492,157.958882254907,25.908972071208,18.6664621792505,24.0030498163055,7.07786223910358,19.038911600185,14.7369526314724,6.62424580998019,3.39018811748358,2.50469629333998,1.08042745420733,154.219137749896,448.618297133222,191.558289337744,364.603747774919,73.0942291530235,7.32089348729616,9.9578797684916,19.2532803102967,538.795846678014,50.4305175748727,87.1344907792923,346.555988440278,546.945961928485,310.823232831248,22.4325558992965,24.2791568533736,35.6521164424201,17.3918445250979,8.69375314421607,399.720938467422,526.777881719318,401.794459168395,719.808000741317,18.2770036915165,101.11602250604,110.981039701029,76.9791915487269,4.92297481221929,5.13706756616446,10.358720421766,11.8055091787609,11.1404715765461,1.36509120114109,7.5389841207577,17.8843974370938,8.1870262235377,31.356484059229,38.0252884114466,12.489077842437,14.6294439887306,20.2038775072542,6.48411544936568,1.35654505621564,26.3215606152465,43.1462025172445,57.0557608046243,44.1640354392234,24.0696859345792,13.075142319749,46.8308511405264,43.1997819173806,27.2866795755007,25.4225010504447,9.50586759640941,26.5783545619975,1.2066583739251,12.9772793315647,38.9998106704344,75.212124505393,34.9256668486354,26.9890273783265,19.2211858209061,4.3428366370589,83.3911393715253,92.423534575921,10.6824563146492,4.13281594324241,30.8715521447411,27.2411703420884,6.09134280017084,238.215868280342,464.109480712982,1359.93969044878,329.22565007305,366.919633165398,458.971393356171,309.917068407064,807.764698727498,1194.76346362317,10.3542972872643,15.0094912809773,7.0579790114303,5.47894111783206,8.12776693147866,222.811870731,577.724778113035,756.809633742525,1101.39252589901,5.58221896797328,4.3372231383117,562.670636017958,456.278942792655,708.848224467925,810.699493583544,835.149623137529,325.41240706308,194.651271059752,612.380990905146,187.480876380718,310.969527594679,441.560565659409,578.117369162992,6.60914681614736,300.007577717166,249.961707363887,298.332389131489,399.372749881561,7.479354337555,4.68819162062805,22.1293416993472,363.776306248236,677.913578342912,476.304506411819,228.142995106576,326.570030119654,791.514042663347,752.719359001426,582.642730222486,213.054683152345,6.74469009243728,210.048227588616,485.666902695045,224.525835520234,537.680960850569,524.228307668674,364.501429870937,34.9215929887842,6.39474393790792,6.02302314615713,5.52415589175083,2.17132574627666,4.2216968333923,607.084075608195,641.811225708176,369.377564217892,377.814921538799,540.545311534464,419.367627118975,299.137235155513,3.31672197502804,90.7469933852843,661.692703774021,653.257492265117,150.10469637588,662.052149902549,513.776275940058,514.072848132852,525.895825767704,186.540123659194,132.70392527452,807.294816022879,756.896620982635,432.884897829193,532.388767056206,885.283952215769,544.192266444715,225.714240175371,96.1000715644302,99.0967336031705,697.186516683065,1657.92757899992,73.7183162103557,34.8767839517627,31.8743038312704,12.4048066958836,20.0432309345751,33.6906834215195,30.3566714824741,29.8837326661437,24.6554023945117,174.356692821002,1533.54774261661,576.576370365387,375.210073482276,743.586553309698,367.552547875924,370.431221101655,242.242140652534,487.443857668835,350.623814611175,590.728165026869,3011.78278378619,253.845395737571,631.101701415694,524.301293260834,165.628427833956,10.2549627971966,4.48618064073033,15.4418137614499,390.684028162113,88.2825525212365,689.370192702598,411.270720067034,564.243338627865,377.067962141382,807.023450221036,320.895887970738,641.079699192987,481.289999140757,568.004266618322,159.693718712162,391.98738756549,205.901855018191,21.5773927026333,25.1546991873375,21.7084783762832,496.461597126808,516.262576759512,436.816146891248,605.190867223422,817.298641441218,1434.61874885043,2.67176659667874,408.095249780254,613.808491780074,484.799130839443,861.724791384036,3.89229399448788,1.58290522974017,5.65021318393805,5.46815395121677,17.5600231278489,436.573749042612,438.555954455198,1094.53125264659,458.133730485877,35.7257904207193,309.442456877087,103.696469104691,34.0386231890516,65.056877817884,116.548999982016,320.095512680276,694.659183627405,1100.49415153441,501.466748007145,38.7825581702833,8.80684916780343,2.08843670306533,6.93539199039663,507.206598644405,640.37433318068,1020.65809514089,491.60952590769,776.462475356805,819.610007859583,572.476892011554,180.417373698012,487.20574192112,4.45154095306305,11.1257043699249,2.11624394427438,417.250930815562,62.1699494592493,5.9506439882319,18.5599409199362,19.0407287891697,11.3241782361375,8.40849528012968,19.6601063401767,41.8569438357025,21.8814537828366,6.21870591601784,49.2439388213583,45.5675495352826,34.2951439734891,18.5547471966525,1643.72289526178,18.2048629588018,102.775007596613,672.655316040179,436.157715310464,381.290554599276,378.274436666732,584.367445755834,188.93245507922,588.483936610437,462.137531764589,729.874273411451,656.895728328976,578.308173092656,503.930675454525,763.843836453186,39.2697626011671,549.69136920578,222.394205211057,108.097269278401,12.4640120805048,11.8374182534401,14.7776912508101,17.4900252253253,22.8274956324958,24.7780360996226,5.87496567728897,9.39495513984086,9.11630613391895,31.5928825323796,25.3061200227826,1.9719548102663,5.62370030290178,0.586487245688403,2.64254243321861,3.00812066873699,11.2183362766906,5.49155912825841,7.31502745301497,20.8774064027406,20.5850543925686,12.141143548548,0.712434488891904,9.89794840006895,36.4317128785092,22.7994092917731,15.3427653687805,15.170346826675,13.7556513287857,6.80990864611646,9.89149320661464,0.903969837158064,3.44968261842026,5.78398823640168,14.5022126181157,16.4256958052649,9.8408029999523,17.2788100141074,24.8937802622831,25.1120873870739,3.12842883548277,13.0498041562443,12.8467809938746,3.35689097396403,2.37898677024824,6.93324146992631,3.87141538994862,11.3957645593818,6.29801022980635,503.807301725692,422.378946339109,733.014381436348,428.771256812865,433.714170371413,419.324106090238,4.35525403412228,2.61241122759304,8.20581441632534,2.90013097278933,150.222571539814,31.2725237855874,20.1340587644968,29.4532757671228,21.6886325901532,7.97263548939117,10.4344725051305,5.99783681583384,2.74995080768367,15.4693844233584,25.99532902595,18.7739768238408,18.2531112283068,9.12096175270912,2.56853202569534,3.25790496409706,5.409410208722,19.3592631394868,30.0564475583924,24.7141597402234,35.2908262699677,40.3772605417997,17.5441163980396,37.4093650674964,23.026424541929,394.96672300573,337.365275393248,674.881890076239,265.458326370966,191.185722880099,73.3141355334463,393.708926684177,1189.20865580048,16.6863818029034,13.5179176238159,241.916616601683,588.632432462475,621.930656197878,352.267200475121,561.781398218686,395.959549193444,658.810907205895,675.141795951241,4.23454133534557,4.57130626318012,4.35004746232533,9.34430658538159,2.74114022855484,10.9101152056371,3.52891986429618,10.026558586216,13.2032656500979,3.8029166661292,15.6632977943797,120.027923700747,195.854099261171,23.7446845241232,195.632400093236,5.21463480864716,18.1344033228466,7.60941279498349,4.28661200271313,5.24578546576322,17.4014589673647,364.611037793496,238.441075726396,149.856753696391,104.424886071808,81.7656608115883,395.412895873968,279.697075630267,293.36860240272,535.689083844946,521.892358858866,350.398509504681,48.8027228123345,62.1679956461887,5.69441818680723,33.8304089001856,10.3590080374001,25.9179341735247,6.45941817572364,8.58620744283185,8.48895627129062,8.17225814321491,9.04285925699825,11.4755496517102,4.3949545458557,7.24255174195252,33.9524180433727,22.7504403657171,33.6008383643613,56.4275363582027,11.7175559486145,49.6338337963656,13.2385403061063,10.6698303096766,19.5561357468193,49.2262751756597,68.028085122147,11.3790917663131,6.29652041475416,3.92226017547398,7.8933399071019,4.27353509505756,7.27458170663548,4.64272883262166,29.1419888454533,11.9391146356829,29.5763376678746,15.9340745765713,1.18838163426019,54.0131916374274,343.447889883866,48.1456006334745,14.3842665436939,306.112189622908,141.451723394757,94.36847566301,101.854663954831,184.654189751058,314.214399241505,542.322069205515,9.54021210586037,4.41650317337457,67.2214352982851,81.6092321098356,80.305842004131,146.491045722194,55.852208703551,3.55955436519875,5.39430558042262,2.94610579371139,13.8101434239702,0.96481287947524,39.5392525174215,30.3379990276753,689.269464838193,236.92981634364,403.135988042788,574.086534005256,708.78496870603,317.3223013477,892.04990001524,623.111180487488,613.470293022801,362.986122874118,176.016032466082,83.5247415779235,483.715193521782,258.500842268393,36.8348980837772,20.578500897505,13.9762975495065,5.70941264282816,18.079716354918,14.7667154690999,12.1088164052431,15.3132507761189,8.13266955951459,57.267772710369,30.3522944763904,23.3587630079008,12.4870055250626,13.6661191442112,12.9575174477607,652.141346626687,639.637758318694,383.348472250832,1571.53677058931,34.0914419647971,17.6280676384913,12.676367824733,17.6671921080882,49.6947627387791,41.4474664363086,72.9853125511683,74.6979689386345,246.019589739871,359.406968985137,375.938905399927,2949.71570561155,598.98940072472,544.690062428387,354.979784164529,71.2841015503665,590.559250857271,128.896251466493,901.814779863266,155.354490849561,705.26008514044,279.217785310834,201.541592733439,3.33012969235597,15.118491790369,20.9525273902219,7.49445405922486,43.617591065138,47.1810635635374,15.7479320564501,5.00289919415455,13.8910906125424,6.82367151683956,4.77262642369974,4.25315779867136,377.378968342401,281.55022436784,236.217954729474,479.123496237869,102.547692381047,35.6814542292061,17.5199409544194,122.339914226196,296.156044951178,264.550777947861,14.4401601920354,5.65057765747713,4.52179693712,6.01458961546571,6.79836052023148,6.65134666765818,2.47066274640379,0.334868838115126,5.1020711393527,6.3028838068188,36.6289704845613,46.517119875913,24.8431609128583,21.5678851874269,137.420006033311,114.324599911357,24.8507725666944,48.8850441132801,10.1493312329264,7.69900951253227,27.882283856366,29.5563273056453,11.6071850515101,7.3159519191504,16.1229395212024,5.66213091540645,1.44897444363098,9.16199064376752,10.9075041715709,14.5117178872749,29.3181226875307,25.6438028932278,588.432354692331,154.089385674711,420.567990320757,207.203633480563,17.8787986026072,11.2097927276755,1.64121088376041,10.1781338565179,13.5871988085008,10.9724492073358,20.8762699180044,12.2037980364939,8.64708692698601,11.1413780805504,5.70901753873614,7.36506130545401,234.836986881221,261.406899231003,719.695131510266,394.936811090453,699.638282237081,289.144788332352,382.079684678481,400.797251966882,376.673123736289,31.761175233721,32.3971860950444,26.5660111705112,15.2653870602504,10.1489914625848,5.48691893022874,3.12485000128511,13.0677336775409,3.97762367357568,7.06974854673347,4.49505760690122,24.3010196555491,110.252114414623,87.8417120551939,233.147738082923,304.926266138847,54.0173780420276,120.637991045958,124.72448085231,230.803631926023,358.606525795067,326.881243801089,113.158901925314,188.320190635871,871.442158509177,293.175211053297,27.7644314666036,1.19913965593561,12.7639843411163,9.57612855861267,13.0161673218663,9.78160453370612,3.25385788663914,7.22897542565067,5.23775430309079,6.49440005466722,3.9433020073044,14.3284683180817,12.3443204305435,21.6564618910482,20.0429139456452,2.67968560082575,15.5289223284368,8.40009820555232,4.18207749337478,4.60588347075351,10.2487416116882,14.9081749450676,6.25803961770368,7.2591062716391,3.87152973425939,4.05295763550151,3.5995454692746,5.40213637799654,7.85483034262703,10.5613718529629,14.2845987213957,76.9784162947966,72.4406984271198,12.3401270567261,12.572733861532,1.90932878826063,4.01632865847117,3.35109305389359,7.10773482528374,11.5968343925483,26.653345556325,20.547726518652,12.2207217131781,1286.87911203404,68.1650735747588,22.4521740477215,532.745488612585,246.551071321318,303.340564653272,912.834769301897,356.822344872699,280.309960482526
        };

        // Apply log function to distance
        for(int i = 0; i < distance.length; i++) {
            distance[i] = Math.log(distance[i]);
        }

        // From leroy dataset
        double turnAngle[] = {
                55.9689186124135,-130.934884951845,-159.69984843491,24.8448812668084,-179.169469275345,108.659903353671,166.631235739041,-21.7280061579238,77.7865332076799,-58.7761457016636,-154.408510527922,174.215217842566,-42.1885072052936,85.8671274392711,147.211261731295,42.8863465027599,154.475955005429,-170.968716441394,-69.6594747841864,-165.668794248808,104.734238234914,-170.076388397368,-117.978575636902,117.019000803317,158.06477368448,-82.3092836435155,-175.655759124068,-17.0145075164892,-170.542519318279,113.766503595971,106.522670505514,158.340537816893,176.173686671055,158.837650343992,129.55087849239,91.6924584794548,108.930918079815,156.837578920336,91.6284483857404,119.962981695826,61.1246071144171,177.495130054355,-159.751063280526,63.0789192900561,-106.712884758101,81.5597610169349,164.887163483967,-147.120610742,101.067915608402,155.078392440205,95.6323921805619,-155.810126253293,45.1814705969123,-68.4200002238653,149.850437655529,31.0779614854841,1.77803255656335,-24.7317681883698,-0.474354672454808,-47.1206032752362,177.050697995509,161.606141950614,45.9613513976613,-173.106547051106,5.2394544637184,-82.917009759734,12.435255101128,11.8624553475382,-96.7901270859786,97.7948735959786,6.34349378839065,-51.7797435637727,32.7390789803851,-2.16058545096305,-28.5962492797462,17.1480728731428,-66.239100041828,13.2223377233519,-146.504098536704,-34.9471637280051,-89.156801231693,175.36354193377,-73.2626519945466,137.560796554241,-177.184923373158,-107.18742524477,-48.1297666818077,39.3209614788045,-93.3935942963179,-5.13398553096778,-69.3172972097599,3.32669827232803,-19.7351141556238,-171.304935356619,-144.619223334725,104.509402662101,-151.02290876997,-127.499404871283,176.704005816024,-179.361862759544,-43.4740394500645,-129.437742072345,-168.459057490185,-2.19333561234228,10.3053269048805,-89.108184372963,-61.624257834526,-11.318318398947,167.744233384117,110.050919262153,1.5469423265846,-178.121335594645,-177.156242062425,157.078090899967,-152.845996691478,160.8424689546,37.2686702248199,36.3307131957508,19.7508764213825,-14.9237481795103,42.2286020856604,113.698522132053,-74.7897794878054,37.2808595984238,6.92156792792292,-42.497704653648,-0.122232723609102,36.1047609218717,83.0875074000867,-58.7135079375069,20.4645899106152,136.869414475721,9.80114380137144,-111.928725717736,-175.743531652798,-179.834637770672,168.224953459249,9.61287646610478,170.134211535295,144.757185852773,126.796128563195,-9.17032136515988,28.340370705005,-119.231330790982,-48.2477215429526,131.345618266561,7.73549195632296,-86.3239775358308,99.2766614081445,-19.1166503598084,-74.7171915522946,84.0933787865722,-3.46026034310194,-11.8141737278162,-167.731889938737,80.789437025149,-18.2475313056561,-164.055748415583,-179.740325504681,151.294448708134,157.910679140022,60.3420574767104,40.5100770772104,124.205843917521,25.9278396477194,-131.563213789978,84.6246063615224,-8.04897606854766,172.769814215213,-47.276907460414,135.311665230109,14.8989429846575,-178.502361812866,7.48342836038594,-11.7480369807174,-176.194983608212,-27.2619267685879,-139.073380770598,-109.907796206946,-89.6090767962247,-157.708753831584,169.805581832532,98.1233554409147,3.03714460778346,-159.105504228295,10.1740534019816,-177.624332544578,151.553307391605,143.632618611297,140.631053784187,46.153121431052,130.457056281057,167.100192315626,105.499816066815,146.119718839938,88.821380980419,78.4869173411493,-163.756541460356,-104.976191411232,-124.601382238328,-29.0819148775173,-107.742998808573,-54.7542658700368,-154.806575572136,150.537191321845,174.589170379754,-78.4339444660719,-14.8738872274577,43.0750076396484,-38.2745038751333,3.8171131554403,162.84307463698,-164.215767678355,-138.642813704326,-50.0818852071395,-157.372729796373,-155.20321424927,58.1898087365112,-145.580144332246,-108.711427916806,-75.862444517175,-171.180165750626,-33.5738825634916,-64.8257212471001,169.730754487642,74.3340593191422,41.1072309261176,-100.504657468923,-3.84808318894051,92.9000465995059,25.9592174017712,99.2481511665387,-123.507564787496,-34.425002947463,-55.2451551224911,11.4160956108711,92.3533882039151,166.013482077946,-142.877323038259,-120.188936167335,147.765347229735,-2.30390407332163,122.520881038608,20.1786570212138,-64.7193754520167,45.4422665941753,-21.1029026089008,11.1352582289911,89.0765586725531,-80.994816875722,171.507655971758,-177.172204170518,-170.704055735254,-16.4175058697536,-3.72483438894352,178.693354867828,85.4665462934538,77.1023489346419,156.95352120176,166.59515684058,-79.1898917903746,-172.345101747323,140.050575524245,157.330993764007,-128.134451085299,163.901298263993,-175.530356898642,151.602402920623,123.381870415381,57.3680429432179,-119.397440938029,-168.626507843811,159.482709750269,167.366265736911,-169.26667625726,-177.286290919896,-116.562107718104,-32.9584343109276,-147.523361226218,-148.595677145219,-144.852298230294,-46.7442188366426,83.8901065179284,142.031459154255,-107.455309100135,173.104066658216,129.313290731557,-148.582450610277,13.2377385556867,-167.013238853295,-73.1802848703176,43.5400014140735,135.56053379344,-33.393415245888,51.2875830071811,-37.5678028409947,56.2774562155325,28.6902867261581,53.0659133260074,54.4609139671808,-152.342403895524,-80.8758818666016,-6.4764346528973,-178.260499381096,177.571236389484,-128.155968873729,-83.055002766235,-16.6513562904177,93.6915480597706,-114.486098887506,125.761842258731,113.99435585253,26.3586352630502,76.5716860606533,-33.5747777413401,-3.93468728089232,-58.7502071125934,-146.067044124821,90.1895530120506,-91.0055751430131,161.83718857758,-24.4743770617507,-33.3177085338585,46.6034350715785,43.4574828661162,-123.693868711873,2.75006753043857,123.81791652092,5.11945775559803,-166.706640211706,-116.810188990951,58.9746539524305,-121.260998363126,30.5366339615571,-71.6429775750069,124.131314957362,-91.3826302320915,1.07203628943222,-36.793070827688,-32.5889078448831,-151.8067925731,53.4980349336911,-95.5108130702499,-48.0663492618319,55.2153651156205,39.2201964546614,45.9375085722168,46.07242188061,175.424237273347,172.647351015087,148.341388198177,-36.404929002878,67.1420040533601,155.436371243578,34.8445365949765,3.22465583633044,1.76889900278786,-88.2472717196057,74.4531120161392,-61.0948240427653,-149.42474401546,-116.135288238905,-34.9445321119126,-98.289665775064,-77.9101133094159,-94.0923940282624,46.005666904809,-18.9336356827917,17.4095808153172,102.077935633233,-12.5528767587251,79.30465252534,-1.74619477507682,15.1846226399438,77.0732338770517,-153.386027100791,-10.4314672053054,8.42773201201874,-137.410596595131,-175.660445270323,-69.1449511361998,86.659454001242,-163.283062931376,79.7860051896605,-3.82052594881125,-90.9834613851898,173.374178667254,81.9670794298258,162.819031304214,4.2175405337853,-163.311524831275,35.8947032005032,-47.6502117722611,27.4817665460417,42.1203378733346,56.3200769932826,6.75929977020121,-23.0973964452434,22.7464780038349,-39.3082639270607,-41.0109117394253,90.5521709095107,0.272196273258601,-44.4826005337683,-8.80646653457947,-40.9926753431792,75.6994500328109,14.9308864325881,42.1910276100873,-179.496166929943,-35.6764989784733,-11.6378571685283,-134.520009910269,-21.828579657688,-0.183618034920585,129.960226216071,17.1476488332516,-15.8960357445634,35.6656069093108,-10.1952735084708,-33.6039992905425,26.482483455948,11.7676041595339,-79.173455200642,177.512685494752,151.787493269806,160.57354122712,159.651273182909,77.9761054372326,1.17308501805726,42.3498423110444,6.61354796600256,2.61240621032533,-70.1551664389248,46.8206038338871,47.738538980138,-58.8464606584395,102.000449150757,146.286001767834,-0.676535005118694,-120.767083992001,157.607208895242,64.6865692485753,-155.130894909687,13.2477215762317,-10.1198893805216,-40.67189208333,109.948298536216,-93.3246008550593,102.270506665512,-102.563197202416,159.722117582822,-44.0969829846449,-48.8324672189553,-51.1366397598674,29.7753295014591,-111.777990251507,157.202323427905,-110.927873548,6.7116489069756,177.042770859867,-177.015509807508,94.9076317407754,-46.7812433478061,127.938154398432,-91.260875726337,-49.3089496556609,-0.411272733924903,157.872386990873,139.606313193187,138.911924327283,138.712887478237,155.020878539128,-137.489433766687,-28.0589398381295,88.3700094507376,169.051146209648,153.838481056182,136.281340894887,12.6989458912388,-114.785494606429,-152.864077160947,163.950942872405,98.7759830964517,17.6530590768648,169.020090211349,64.4927224009402,156.589439745585,15.6018928489488,26.1778933108897,15.1439901984886,125.734481839309,-77.7627906671228,16.7751889484084,21.2339578196843,-5.30321226376515,-33.5386897859996,14.6113360214507,25.3331017318206,-4.26588745628408,17.3730783685728,-36.5066351968556,-36.3840915300362,-24.1475855842044,13.7418945299561,-120.749139516199,-59.6076507712423,-47.3762569556034,178.297401674071,-175.440746272857,-74.4000610124894,168.023840286134,125.929734022178,-160.662832820337,36.6007767846746,177.674839571833,122.200828738877,158.842385718285,164.797334159153,-173.040501597427,153.707417771838,-51.2175385195972,-77.3061524307606,-79.5970016161377,166.475829908567,-172.981979774359,-112.836348226116,125.112859236871,160.059030065361,159.957318963877,175.575094819172,34.3626393732538,-159.752332425474,-170.543504163737,135.677121511146,176.284061226942,127.624919584487,-158.407039853227,-10.4722193799341,115.921136840099,-68.2032156929565,44.5063075691573,154.175972416337,-28.0581193601487,-165.440189504627,-3.77993689426032,-31.2564123885092,-168.809459424132,-6.19163443933624,-99.9328472224603,-174.064242023768,-2.35018409170942,-49.1922857092272,-142.313684951418,174.420466486934,-151.110765221003,34.5101430212443,163.696216553383,-10.8132939261417,-28.8185485903674,-1.60185574606561,51.0277384549709,77.2017744485879,-168.215953997983,-173.640059842451,-163.77646363058,-17.0847220287016,-18.8364649513604,-112.710423499602,-16.2265328685513,-126.894400749783,-150.883537368906,88.7697653846445,168.547922350882,176.763973194913,-150.68527129155,-29.0498157167819,-157.928897030214,148.063919148046,152.315130132098,110.182988072546,128.368856218964,-146.9696669013,129.624981838379,68.5360810442537,154.327026803045,31.097159979334,146.633061846634,37.0308978494218,90.9552793339561,98.4728802692656,160.375473071607,-142.372871735424,29.5785670822921,-20.7238661202593,21.0045564324712,-84.0469239699785,-36.9165328455983,160.068781731957,91.0819989524742,118.128461368532,157.544023918157,99.4747835181353,28.6065499942627,-24.6391556706222,60.6860267445425,-123.682561869285,63.5585913170598,-22.9538682155501,28.8502110655366,113.019955794926,-18.5111094606847,173.615510142747,-24.1054827474522,55.5783990088299,144.711956380556,164.660808434822,178.842207876739,160.866837639336,53.7532689501146,123.460683627454,4.61936505187921,-66.3579875729153,52.2316552475775,-147.990698897371,-67.5822759428126,156.00701601731,-173.503913436057,98.1751607706271,-98.4400221011163,49.9787982149621,-173.316610533559,-26.8533693779861,-155.768550431729,-89.4722431803643,-144.359698774147,130.706806242055,-31.5751369249328,96.1238256577702,8.65483570282174,-25.0747772397241,45.6533223349156,-122.826792138314,171.99970371334,-16.8210241463245,165.132279918392,114.801195088319,63.7545547445113,115.302234873425,-159.007078029343,147.655764364194,38.7595400743494,-168.873098279407,-95.4786624601842,37.6145889832163,117.918212446333,134.722164891338,7.7160567020897,-5.75446004965241,162.370381575527,-63.8753776395001,-133.069123690159,142.40735906965,159.866993963591,-171.680510906829,-87.5709032773195,-165.509978124309,171.401475605377,-170.083343115415,-93.5431256498114,-165.670833978938,81.5447439001352,126.827498261984,137.511217760425,-81.6010565773423,-159.792185330437,-27.3014768679187,-175.220889180858,7.73809655849476,-32.4506723323603,-106.948842823314,33.5397160049163,-20.6884531873025,175.30210625748,-0.598271648515805,-126.279843578335,163.643465108366,-28.1864252237061,-23.1392680040623,28.4787790765193,-64.5433313436622,-167.615734055587,-167.279625024829,-176.383675752195,-9.3216269328567,-177.065377539922,-170.074261370707,-161.05132405632,-4.01817349877325,174.825697883647,-117.391831994489,-8.19757023660566,126.603035003877,137.215787186535,59.0399157828413,132.599777976762,-33.1055049818182,-22.5465139672312,13.353586952982,20.3142958584061,-31.8211987207083,-8.04964897938703,-58.5011043644715,-99.100853546944,32.3700708217475,11.765777931191,102.416113174495,-122.948968658309,11.2162152138472,-177.158722902582,-0.853196908050961,-102.21626122727,177.585246512882,-131.155388776391,-53.8447928658255,-113.10370134429,2.50048496139866,173.353276751731,162.133849406062,69.7973907212022,-63.7281762560925,-132.954841865433,22.3321545274162,-10.0414652367446,-0.951870907584663,-58.7516070308509,-133.484611051838,170.208268456127,163.826285495932,-176.700794371801,47.8295752070795,141.211287842747,172.736811142366,136.959680374989,175.453846758374,65.9648363257827,71.5253323823461,-15.922041372716,49.8657327081773,-2.01758440710245,43.3293445675831,142.731342601855,144.564244123254,57.6657855678944,-55.0824306784446,109.41940707988,-15.1503237287513,-24.4430962536921,96.1838867752942,9.97126715520824,-103.368985350566,-27.0283916692097,179.035454417509,176.620639545843,64.8555169817972,162.129072110766,141.269650164682,-160.994491402833,89.1416769649262,-176.038634131034,-135.677389412048,88.0966891510526,44.6891121892104,-75.7474514999177,-31.3528580710976,38.0789045697861,44.7532790862419,-60.9863039213359,179.590646245918,93.1813958085314,1.38990059050428,22.980135670709,45.8072539348458,173.31882707929,140.003384752373,3.5776817356313,-179.82748480261,-32.3791152730983,156.124041150481,-34.1369105500103,151.144176342941,45.5295815138913,-75.0963566059228,-177.056218625425,97.4322619485441,-157.374963249728,-117.709794962796,174.121043071783,99.18439823691,146.051165293749,178.430667863119,136.243944824425,-171.443976212272,-10.083924557741,162.650309093892,-0.123784157717068,-98.6406523862072,-156.612338207261,-141.37790347826,-51.7736823509035,172.079794686148,-160.122751006063,153.41448975007,171.34210696822,76.9985157347887,16.9638884021339,-153.883647020632,39.5791528202675,-32.7879275186469,-164.010171032073,-61.6767445815437,135.166995650382,-88.5229987058655,-31.8925821273103,-132.871178676847,-149.536452043333,154.328840101908,20.711622541569,112.328529947651,119.988997336635,122.569127298075,-61.3509482199675,7.53908243371819,-32.1082457459792,6.5463290979863,-19.5641105047802,32.2861281666208,49.3999707266702,3.53703299390054,55.1717461365075,-149.842866866979,-116.075356464254,-165.250655951395,-64.9814134774067,-52.4079292832466,161.758666623779,38.1883308059183,-53.199177967185,-159.735242093228,44.2254775521226,97.0651706892844,-173.707867953659,-177.753084951565,-13.1954698659778,-10.9735295730632,5.55760758440101,49.1678665306034,176.677105484308,160.37088676927,75.8004333842562,136.32456078098,-102.146242302155,166.155468692432,47.4539749238906,23.4169206146037,127.113101805006,-12.7747703396984,-150.579904713999,-146.216992387415,178.946655854857,-121.730474948963,-118.181144672695,-83.434380611515,80.7645889659821,78.1771612974045,124.736426353852,61.5939791882433,172.377897417783,81.4865016916452,164.332759535475,92.2559890425835,166.482652512868,-152.588989179086,-65.84942833947,47.8018021663449,170.515935613766,179.29277277957,121.603632564261,116.270792083648,-51.4691261339472,-113.965627076992,-96.7648231369933,-111.848168591801,-157.80878810256,-151.403949260852,175.598694649087,-143.143668513501,177.898895169813,43.3119684458901,162.148923027852,110.172902764906,153.709510758738,93.6854483819394,153.204343854581,59.3047957774947,151.892688071307,171.982547273357,95.3216727480819,122.7086910606,53.0277883722926,93.546542414085,144.241743291595,-46.3948739315339,-70.8025484522129,-104.355230519756,79.0686259602112,160.380896802563
        };

        print(distance, "distance");
        print(turnAngle, "turnAngle");
        percentile(distance, 0.01);

        return getDensity(distance, turnAngle);

    }


    /**
     * GET DENSITY
     * Function to get the probalities (see R code)
     * @param stepLength
     * @param turningAngle
     * @return
     */
    private LocProbTupel[][] getDensity(double[] stepLength, double[] turningAngle){
        if (stepLength.length != turningAngle.length ) {
            // TODO Warning: "Vector lengths of step length and turning angles do not match."
        }

        //double bwx = 2* (quantile(turningAngle, 3/4) - quantile(turningAngle, 1/4)) / ( Math.pow( turningAngle.length, (1/3) ) );

        return null;
    }


    /**
     * PERCENTILE
     * Computes the p-th Percentile. Algorithm copied from Statistics I lecture (Brüggemann, Economics)
     * @param array
     * @param p
     * @return
     */
    private double percentile(double[] array, double p) {
        Arrays.sort(array);
        int n = array.length;
        double percentile;

        // Computes p-th percentile as told in statistics I
        if ( Math.floor(n*p) == n*p ) {
            percentile = (1/2)*(array[(int) Math.ceil(n*p)] + array[(int) Math.ceil((n*p)+1)]);
        } else {
            percentile = array[(int) Math.ceil(n * p) + 1];
        }

        return percentile;
    }




    /**
     * PRINT ARRAY
     * Method for printing an array
     * @param array
     */
    private void print(double array[], String name){
        int s = array.length;
        System.out.print("" + name + ":  [");
        for (int i = 0; i<s-1; i++){
            System.out.print("" + array[i] + " ,");
        }
        System.out.print("" + array[s-1] + "]\n");

    }




}



