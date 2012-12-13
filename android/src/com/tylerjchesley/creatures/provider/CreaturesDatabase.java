package com.tylerjchesley.creatures.provider;

import com.tylerjchesley.creatures.provider.CreaturesContract.CreaturesColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Author: Tyler Chesley
 */
public class CreaturesDatabase extends SQLiteOpenHelper {

//------------------------------------------
//  Constants
//------------------------------------------

    private static final String DATABASE_NAME = "creatures.db";

    private static final int DATABASE_VERSION = 1;

    interface Tables {

        String CREATURES = "creatures";

    }

    private static final String[] CREATURES = {
            "I've noticed a lack of chinchillas in this subreddit, so here's mine.~http://i.imgur.com/L0813.jpg~http://i.imgur.com/L0813.jpg~1~0~1355405379411",
            "Reddit needs more giraffes~http://imgur.com/WyvvS~http://i.imgur.com/WyvvS.png~1~0~1355405356625",
            "Aww this is sooooo adorable!! ^_^~http://intesrr.com/img/TRNS.jpg~http://intesrr.com/img/TRNS.jpg~1~0~1355405146018",
            "Little guy looks tuckered out.~http://imgur.com/h8ux7~http://i.imgur.com/h8ux7.png~1~0~1355405080258",
            "A baby Dwarf Rat (Rattus Tyrionus) next to a wine cork for scale [3264x2448]~http://i.imgur.com/3HfGD.jpg~http://i.imgur.com/3HfGD.jpg~0~0~1355373290257",
            "The Spectacularly Colorful and Surprisingly Small Peacock Spider [1024x628]~http://i.imgur.com/01IKl.jpg~http://i.imgur.com/01IKl.jpg~0~0~1355373262697",
            "Hey, lookit me!  I'm a pika!  [1024x768]~http://i.imgur.com/OHR5D.jpg~http://i.imgur.com/OHR5D.jpg~1~0~1355373231813",
            "Fennec Fox [1944x1296][OC]~http://i.imgur.com/aBRUq.jpg~http://i.imgur.com/aBRUq.jpg~1~0~1355373220765",
            "Baby lion plays with snowball in Belgrade zoo [4000 x 2749]~http://i.imgur.com/LL8DC.jpg~http://i.imgur.com/LL8DC.jpg~1~0~1355373203485",
            "Duckling [OC,  4752 x 3168]~http://imgur.com/xsR75~http://i.imgur.com/xsR75.jpg~1~0~1355373179428",
            "Four night monkeys (Aotus lemurinus) squeeze themselves into a hollow in a tree trunk 25 feet above the floor of the Ecuadorian Amazon rainforest. [682x812]~http://i.imgur.com/ypa5Z.jpg~http://i.imgur.com/ypa5Z.jpg~1~0~1355373159640",
            "World's Tiniest Chameleon (Brookesia Micra) [2178x1432]~http://i.imgur.com/REkjd.jpg~http://i.imgur.com/REkjd.jpg~1~0~1355373145993",
            "Rare Golden Brushtail Possum [960x639]~http://i.imgur.com/syquN.jpg~http://i.imgur.com/syquN.jpg~1~0~1355373131198",
            "Crocodile and Butterflies [1920 x 1200]~http://i.imgur.com/COasI.jpg~http://i.imgur.com/COasI.jpg~1~0~1355373120016",
            "Cute black cat [500x473]~http://i.imgur.com/Mmfl8.png~http://i.imgur.com/Mmfl8.png~1~0~1355373086622",
            "Arctic fox [498×750]~http://i.imgur.com/sizGN.jpg~http://i.imgur.com/sizGN.jpg~1~0~1355373070763",
            "White Stoat (Mustela erminea) [620x620]~http://i.imgur.com/Htwib.jpg~http://i.imgur.com/Htwib.jpg~1~0~1355373059165",
            "On Lord Howe Island black rats made tree lobsters extinct, now 2001 scientists discovered 24 still living ones under a bush on a small&amp;rocky island \"Ball's Pyramid\" and resurrected the former population in Melbourne Zoo [624x833]~http://i.imgur.com/KpxWn.jpg~http://i.imgur.com/KpxWn.jpg~1~0~1355373039742",
            "Moose Babies [720 x 1116]~http://i.imgur.com/8fy7d.jpg~http://i.imgur.com/8fy7d.jpg~1~0~1355373028191",
            "Baby Octopus [1600 X 1200]~http://i.imgur.com/WrCba.jpg~http://i.imgur.com/WrCba.jpg~1~0~1355373015542",
            "Sand kittens (Felis margarita) at the Ramat Gan Safari near Tel Aviv [3500x2333]~http://i.imgur.com/4M6Ln.jpg~http://i.imgur.com/4M6Ln.jpg~1~0~1355372971838",
            "Skewbald Foal, Dartmoor - UK [1280x1920] [OC]~http://www.flickr.com/photos/m-wolstenholme/7780427816/lightbox/~http://farm9.staticflickr.com/8292/7780427816_3c7da02a2b_m.jpg~1~0~1355372837989",
            "8 Day Old - Pygmy Hippopotamus (Hexaprotodon Liberiensis) [1725 x 2300] [OC]~http://i.imgur.com/cmOCe.jpg~http://i.imgur.com/cmOCe.jpg~1~0~1355372762762",
            "A newborn Anteater baby relaxes on its mother's back at Bergzoo Halle, Germany. [1500x1000]~http://i.imgur.com/hkM7N.jpg~http://i.imgur.com/hkM7N.jpg~1~0~1355372658647",
            "Short-tailed weasel (Mustela erminea). [2488x1587]~http://i.imgur.com/OTQmY.jpg~http://i.imgur.com/OTQmY.jpg~0~0~1355372462124",
            "Just a bee, by me. [OC] [2939x1512]~http://i.imgur.com/g7bXE.jpg~http://i.imgur.com/g7bXE.jpg~0~0~1355372424887",
            "Wine-Throated Humming Bird [435x580] (x-post r/pics)~http://i.imgur.com/j3shP.jpg~http://i.imgur.com/j3shP.jpg~1~0~1355372391398",
            "Borneon Dwarf Kingfisher (Ceyx erithaca) [1200x900] [OC]~http://i.imgur.com/n3EUc.jpg~http://i.imgur.com/n3EUc.jpg~1~0~1355372319029",
            "A playful red ruffed lemur (Varecia rubra) [720x960][OC]~http://imgur.com/FazVs~http://i.imgur.com/FazVs.jpg~1~0~1355372256340",
            "Belly rubs!~http://imgur.com/ZKPbB~http://i.imgur.com/ZKPbB.jpg~0~0~1355371658388",
            "A day in my shoes~http://imgur.com/deCO6~http://i.imgur.com/deCO6.jpg~0~0~1355370461798",
            "One of his favorites spots ~http://imgur.com/lz9p3~http://i.imgur.com/lz9p3.jpg~0~0~1355358067604",
            "I thought you might appreciate a picture of Oreo, yawning.~http://imgur.com/jSTsj~http://i.imgur.com/jSTsj.jpg~0~0~1355357975366",
            "He's all tucked in for bed~http://imgur.com/BFnC2~http://i.imgur.com/BFnC2.jpg~0~0~1355357955118",
            "This is Cupcake, the sweetest bunny I have, she turns 1 today, so I thought I'd share one of her baby pictures. She is 4 weeks old here.~http://imgur.com/wTUWj~http://i.imgur.com/wTUWj.jpg~0~0~1355357936616",
            "I think I found my favorite cross - pomsky~http://imgur.com/UBRvI~http://i.imgur.com/UBRvI.png~0~0~1355357822195",
            "He was way too cute to consider eating.~http://i.imgur.com/rlZB2.jpg~http://i.imgur.com/rlZB2.jpg~0~0~1355357809860",
            "Weeeeeeeee!~http://imgur.com/lOscb~http://i.imgur.com/lOscb.jpg~0~0~1355341055158",
            "Little Fox~http://i.imgur.com/loJp1.jpg~http://i.imgur.com/loJp1.jpg~0~0~1355329754468",
            "Just a kitten with bubbles~http://imgur.com/FgC3A~http://i.imgur.com/FgC3A.jpg~0~0~1355329720136",
            "The adorableness of baby meerkats. ~http://i.imgur.com/mqc8I.jpg~http://i.imgur.com/mqc8I.jpg~0~1~1355329698982",
            "Shy boy~http://i.imgur.com/jNNCy.png~http://i.imgur.com/jNNCy.png~0~0~1355329342855",
            "A ball of red panda~http://i.imgur.com/nHvju.jpg~http://i.imgur.com/nHvju.jpg~0~1~1355325532928",
            "No more thirsty~http://i.imgur.com/GKRII.jpg~http://i.imgur.com/GKRII.jpg~0~1~1355293125099",
            "om nom nom nom fingers! - Imgur~http://imgur.com/VSnAH~http://i.imgur.com/VSnAH.jpg~0~1~1355289931790",
            "Just a cute lil bunny butt.~http://imgur.com/pSQAR~http://i.imgur.com/pSQAR.jpg~0~1~1355263702738",
            "Is that... for me? ~http://i.imgur.com/KWbUY.jpg~http://i.imgur.com/KWbUY.jpg~0~0~1355263283030",
            "Little ball of fur~http://imgur.com/6u9P6~http://i.imgur.com/6u9P6.jpg~0~0~1355263264933",
            "I've never squee'd before now~http://imgur.com/8qHS6.jpg~http://imgur.com/8qHS6.jpg~0~0~1355244930932",
            "Catlove.~http://imgur.com/pLgY6~http://i.imgur.com/pLgY6.jpg~0~1~1355243662944",
            "this belongs here~http://imgur.com/nPXPs~http://i.imgur.com/nPXPs.jpg~0~0~1355199519444",
            "Dragonfly covered in dewdrops.~http://i.imgur.com/exmwn.jpg~http://i.imgur.com/exmwn.jpg~0~0~1355198902339",
            "I went on a hike in Yellowstone. This guy was curious. r/aww needs a marmot or two.~http://imgur.com/OeVvv~http://i.imgur.com/OeVvv.jpg~0~0~1355147367952",
            "Brazilian Tapir calf @Dublin Zoo... [2300x1314]~http://entertainment.ie/images_content/DUBLINZOO%20008.jpg~http://entertainment.ie/images_content/DUBLINZOO%20008.jpg~0~0~1355132863128",
            "A baby Amazon Milk Frog (Trachycephalus resinifictrix) [720x477]~http://i.imgur.com/oaCrs.jpg~http://i.imgur.com/oaCrs.jpg~0~0~1355132751079",
            "Regal Jumping Spider, Phidippus regius, [1572x1600][OC}~http://i.imgur.com/PUiV4.jpg~http://i.imgur.com/PUiV4.jpg~0~1~1355121246708",
            "Colorful Fly (x-post from r/MacroPorn) [2880x1800]~http://i.imgur.com/GT0ZY.jpg~http://i.imgur.com/GT0ZY.jpg~0~0~1355120827352",
            "Baby Koala [800 x 1203]~http://i.imgur.com/X9rjz.jpg~http://i.imgur.com/X9rjz.jpg~0~0~1355120692336",
            "Meet Beau, an orphaned 40-day-old echidna puggle who has been saved by Sydney's Taronga Zoo. [2000x1333]~http://i.imgur.com/SxqQy.jpg~http://i.imgur.com/SxqQy.jpg~0~1~1355120311472",
            "Grey Seal Pup at Donna Nook, UK [640x480]~http://www.flickr.com/photos/sameklund/8221687234/in/photostream~http://farm9.staticflickr.com/8483/8221687234_25edf2831d_m.jpg~0~0~1355119921993",
            "My glider likes to hide places...~http://imgur.com/4bElG~http://i.imgur.com/4bElG.jpg~0~1~1355119518022",
            "My two new best friends~http://i.imgur.com/tVEMW.jpg~http://i.imgur.com/tVEMW.jpg~0~0~1355119498545",
            "Our little Rex. Frankie~http://i.imgur.com/dW9EO.jpg~http://i.imgur.com/dW9EO.jpg~0~0~1355119015093",
            "Herr Bruno, Das biggest bunny~http://i.imgur.com/yltrT.jpg~http://i.imgur.com/yltrT.jpg~0~0~1355118878114",
            "Baby Marmoset~http://imgur.com/f42zc~http://i.imgur.com/f42zc.jpg~0~1~1355117081755",
            "A Bush Baby! ~http://imgur.com/pOfZ4~http://i.imgur.com/pOfZ4.jpg~0~1~1355115351693",
            "So my friends sister got a baby raccoon as a pet..~http://imgur.com/AsF0X~http://i.imgur.com/AsF0X.jpg~0~1~1355115263020",
            "I don't think this subreddit has enough baby goats. :)~http://i.imgur.com/uc3Y1.jpg~http://i.imgur.com/uc3Y1.jpg~0~1~1355115244342",
            "The sweetest picture I have~http://i.imgur.com/wrtvG.jpg~http://i.imgur.com/wrtvG.jpg~0~0~1355115186571",
            "I wanna be like my mommy!~http://i.imgur.com/UwJlJ.jpg~http://i.imgur.com/UwJlJ.jpg~0~0~1355094864160",
            "Really cute cat~http://i.imgur.com/txHGf.png~http://i.imgur.com/txHGf.png~0~0~1355071450181",
            "Born to be in r/aww~http://imgur.com/VZx04~http://i.imgur.com/VZx04.jpg~0~0~1355031742351",
            "My lop as a baby~http://lopsandmore.com/wp-content/uploads/2012/01/DSC_0326.jpg~http://lopsandmore.com/wp-content/uploads/2012/01/DSC_0326.jpg~0~1~1355031524237",
            "Mum made me a hammock! ~http://i.imgur.com/6U0a1.jpg~http://i.imgur.com/6U0a1.jpg~0~0~1355031453246",
            "My little Frankie, 11 weeks old! ~http://i.imgur.com/JxWbi.jpg~http://i.imgur.com/JxWbi.jpg~0~0~1355031442200",
            "Salad thief ~http://imgur.com/OKeB6~http://i.imgur.com/OKeB6.jpg~0~1~1355031383754",
            "Red Panda. That is all.~http://i.imgur.com/THao1.jpg~http://i.imgur.com/THao1.jpg~0~1~1355031292568",
            "Getting into the Christmas Spirit...~http://i.imgur.com/5zpPw.jpg~http://i.imgur.com/5zpPw.jpg~0~0~1355031164571",
            "Just 2 guinea pigs. Having a bath. Holding hands. ~http://imgur.com/1boFQ~http://i.imgur.com/1boFQ.jpg~0~0~1355028172951",
            "So my sister just sent me this picture of her new kitten. Meet Harry ~http://i.imgur.com/QHgIb.jpg~http://i.imgur.com/QHgIb.jpg~0~0~1355028157149",
            "I aww every time I make a Totino's, thanks to my roommate's kitten mitten. ~http://i.imgur.com/iDXiz.jpg~http://i.imgur.com/iDXiz.jpg~0~0~1355028145304",
            "My sleepy Boy :D~http://imgur.com/4AVul~http://i.imgur.com/4AVul.jpg~0~0~1355028130423",
            "This little cutie thinks he's a Christmas tree!~http://imgur.com/Ha582~http://i.imgur.com/Ha582.jpg~0~1~1355028108343",
            "He loves it! :D~http://imgur.com/zq8te~http://i.imgur.com/zq8te.jpg~0~0~1355028050237",
            "Cutest sleeping baby squirrel you've ever seen! More pics in comments ~http://i.imgur.com/SbHwN.jpg~http://i.imgur.com/SbHwN.jpg~0~0~1355028020779",
            "My bunny Boudicca (on the left) is in the hospital tonight. This is her with her boyfriend Milhouse last Christmas.~http://imgur.com/gvRPO~http://i.imgur.com/gvRPO.jpg~0~0~1355027970114",
            "G’Day, Mr. Green Drawers~http://i.imgur.com/6hU21.jpg~http://i.imgur.com/6hU21.jpg~0~1~1355027377028",
            "Googled \"Cutest thing ever\" I bring you: baby alpaca ~http://media-cache-ec4.pinterest.com/upload/69242912990961752_0Y3KHo7n_c.jpg~http://media-cache-ec4.pinterest.com/upload/69242912990961752_0Y3KHo7n_c.jpg~0~1~1355026941426"
    };

//------------------------------------------
//  Constructors
//------------------------------------------

    public CreaturesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//------------------------------------------
//  Methods
//------------------------------------------

    /**---- SQLiteOpenHelper ----**/

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Tables.CREATURES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CreaturesColumns.CREATURE_ID + " TEXT,"
                + CreaturesColumns.TITLE + " TEXT NOT NULL,"
                + CreaturesColumns.URL + " TEXT NOT NULL,"
                + CreaturesColumns.IMAGE + " TEXT NOT NULL,"
                + CreaturesColumns.IS_FAVORITE + " INTEGER NOT NULL DEFAULT 0,"
                + CreaturesColumns.IS_NEW + " INTEGER NOT NULL DEFAULT 1,"
                + CreaturesColumns.CREATED_AT + " INTEGER NOT NULL,"
                + "UNIQUE (" + CreaturesColumns.CREATURE_ID + ") ON CONFLICT REPLACE)");

        for (String creature : CREATURES) {
            database.execSQL("INSERT INTO creatures (title, url, image, is_new, is_favorite, created_at) VALUES (?, ?, ?, ?, ?, ?)", creature.split("~"));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {

    }

}
