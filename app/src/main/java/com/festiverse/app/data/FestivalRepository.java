package com.festiverse.app.data;

import android.graphics.Color;

import com.festiverse.app.R;
import com.festiverse.app.model.Festival;

import java.util.ArrayList;
import java.util.List;

/**
 * Bundled festival dataset for 2026 (BS 2082–2083), with English and Nepali
 * descriptions and bundled illustrations (fully offline).
 *
 * Hamro Patro has no public API, so this data ships offline. To go live later,
 * implement fetchRemote() against your own backend — the whole UI reads
 * through getAllFestivals(), so nothing else changes. Festival dates are
 * lunar and shift each year; update annually.
 */
public class FestivalRepository {

    private static FestivalRepository instance;
    private List<Festival> cache;

    public static FestivalRepository get() {
        if (instance == null) instance = new FestivalRepository();
        return instance;
    }

    public List<Festival> getAllFestivals() {
        if (cache == null) cache = build();
        return cache;
    }

    public List<Festival> fetchRemote() {
        return getAllFestivals();
    }

    /**
     * Replaces bundled dates with remote-verified ones (matched by name).
     * Returns true if anything changed.
     */
    public synchronized boolean applyRemoteDates(java.util.Map<String, String> remote) {
        List<Festival> current = getAllFestivals();
        boolean changed = false;
        List<Festival> updated = new ArrayList<>();
        for (Festival f : current) {
            String newDate = remote.get(f.getName());
            if (newDate != null && !newDate.equals(f.getDate())) {
                updated.add(new Festival(f.getName(), f.getNepaliName(), newDate,
                        f.getBsDate(), f.getCategory(), f.getDescriptionEn(),
                        f.getDescriptionNp(), f.getLocation(), f.getImageRes(),
                        f.getColorHex()));
                changed = true;
            } else {
                updated.add(f);
            }
        }
        if (changed) {
        java.util.Collections.sort(updated, (a, b) -> a.getDate().compareTo(b.getDate()));
            cache = updated;
        }
        return changed;
    }

    private List<Festival> build() {
        List<Festival> f = new ArrayList<>();

        f.add(new Festival("Maghe Sankranti", "माघे संक्रान्ति", "2026-01-15", "माघ १, २०८२", "Hindu",
                "The winter harvest festival marking the sun's northward journey and the promise of longer, warmer days. Families gather to eat til ko laddu (sesame sweets), chaku (molasses), ghee, yams and sweet potatoes — foods believed to bring warmth and health. Devotees take holy dips at sacred river confluences, especially Devghat and Sankhamul, washing away the old year's misfortunes.",
                "माघे संक्रान्ति हिउँदको फसल पर्व हो, जसले सूर्यको उत्तरायण यात्रा र लामा न्याना दिनहरूको सुरुवातलाई जनाउँछ। यस दिन परिवारजन भेला भई तिलको लड्डु, चाकु, घिउ, तरुल र सखरखण्ड खाने चलन छ। भक्तजनहरू देवघाट र शंखमूल जस्ता पवित्र नदी संगमहरूमा स्नान गरी पुराना पापहरू पखाल्छन्।",
                "Devghat, Sankhamul, across Nepal", R.drawable.fest_maghe, Color.parseColor("#F4A261")));

        f.add(new Festival("Sonam Lhosar", "सोनाम ल्होसार", "2026-02-18", "फागुन, २०८२", "Buddhist",
                "The Tamang community's New Year, celebrated with the rhythmic beat of the Damphu drum, Selo songs, circular dances and grand family feasts. People wear traditional dress, visit monasteries, exchange greetings and welcome the new year with prayers for prosperity.",
                "सोनाम ल्होसार तामाङ समुदायको नयाँ वर्ष हो। डम्फुको तालमा सेलो गीत गाउँदै, परम्परागत पोशाकमा सजिएर नाचगान गरिन्छ। गुम्बाहरूमा गई प्रार्थना गर्ने, परिवारसँग भोज खाने र समृद्धिको कामना गर्ने चलन छ।",
                "Kathmandu Valley, Tamang communities", R.drawable.fest_sonam_lhosar, Color.parseColor("#2A9D8F")));

        f.add(new Festival("Maha Shivaratri", "महाशिवरात्री", "2026-02-15", "फागुन, २०८२", "Hindu",
                "The great night of Lord Shiva. Over a hundred thousand devotees and ash-smeared sadhus from across Nepal and India converge on Pashupatinath Temple for a night of prayers, bonfires, chanting and midnight rituals. It commemorates Shiva's cosmic Tandava dance and his marriage to Parvati — one of the most spiritually intense nights of the year.",
                "महाशिवरात्री भगवान शिवको महान रात हो। नेपाल र भारतभरका लाखौं भक्तजन तथा साधुसन्तहरू पशुपतिनाथ मन्दिरमा जम्मा भई रातभर पूजा, भजन, धुनी र मध्यरात्रिका विशेष अनुष्ठानहरू गर्छन्। यो शिवको तान्डव नृत्य र पार्वतीसँगको विवाहको स्मरणमा मनाइन्छ।",
                "Pashupatinath Temple, Kathmandu", R.drawable.fest_shivaratri, Color.parseColor("#264653")));

        f.add(new Festival("Holi (Hilly Region)", "होली (पहाड)", "2026-03-02", "फागुन, २०८२", "Hindu",
                "The festival of colors welcoming spring. In Kathmandu and the hills, streets erupt with clouds of red, blue and yellow abir, water balloons and music. Social barriers dissolve as strangers smear each other with color, celebrating the triumph of the playful over the rigid. Basantapur Durbar Square is the beating heart of the celebration.",
                "होली रंगहरूको पर्व हो, जसले वसन्तको आगमनलाई स्वागत गर्छ। काठमाडौं लगायत पहाडी क्षेत्रमा अबिर, पानीका बेलुन र संगीतले सडकहरू रंगिन बन्छन्। बसन्तपुर दरबार स्क्वायर होली उत्सवको मुख्य केन्द्र हो, जहाँ सबै जातजाति र उमेरका मानिस एकसाथ रमाउँछन्।",
                "Kathmandu, Pokhara, hilly regions", R.drawable.fest_holi_hill, Color.parseColor("#E63946")));

        f.add(new Festival("Holi (Terai Region)", "होली (तराई)", "2026-03-03", "फागुन, २०८२", "Hindu",
                "The Terai lowlands celebrate Holi a day after the hills, with even more intense color play, folk music and community feasting. Janakpur becomes a living canvas of color, and the celebrations carry a distinctly Maithili flavor with traditional songs and sweets.",
                "तराई क्षेत्रमा पहाडभन्दा एक दिनपछि होली मनाइन्छ। जनकपुरमा मैथिली लोकगीत, परम्परागत मिठाई र रंगहरूको भव्य खेलले वातावरण उत्सवमय बन्छ। समुदायहरू एकजुट भई प्रेम र सद्भावको सन्देश फैलाउँछन्।",
                "Janakpur, Terai lowlands", R.drawable.fest_holi_terai, Color.parseColor("#D62828")));

        f.add(new Festival("Ghode Jatra", "घोडे जात्रा", "2026-03-19", "चैत, २०८२", "Newari",
                "Kathmandu's horse festival at the Tundikhel parade ground, featuring cavalry displays, equestrian acrobatics and gymnastics by the Nepal Army. Legend says the thundering hooves keep a demon buried beneath Tundikhel from rising again.",
                "घोडे जात्रा काठमाडौंको टुँडिखेलमा मनाइने घोडा पर्व हो। नेपाली सेनाले घोडदौड, कलाबाजी र सैन्य प्रदर्शन गर्छ। किंवदन्ती अनुसार घोडाका टापको आवाजले टुँडिखेलमुनि गाडिएको राक्षसलाई उठ्नबाट रोक्छ।",
                "Tundikhel, Kathmandu", R.drawable.fest_ghode, Color.parseColor("#606C38")));

        f.add(new Festival("Ram Nawami", "राम नवमी", "2026-03-27", "चैत, २०८२", "Hindu",
                "Celebrates the birth of Lord Rama, the seventh avatar of Vishnu. Devotees fast, sing bhajans and join grand temple processions. The Janaki Temple in Janakpur — the birthplace of Sita — draws tens of thousands of pilgrims from Nepal and India.",
                "राम नवमी भगवान विष्णुका सातौं अवतार श्रीरामको जन्मदिन हो। भक्तजनहरू व्रत बस्ने, भजन गाउने र मन्दिरहरूमा शोभायात्रा निकाल्ने गर्छन्। जनकपुरको जानकी मन्दिरमा नेपाल र भारतबाट हजारौं तीर्थयात्री आउँछन्।",
                "Janakpur, across Nepal", R.drawable.fest_ram_nawami, Color.parseColor("#F4A261")));

        f.add(new Festival("Bisket Jatra", "बिस्केट जात्रा", "2026-04-10", "वैशाख, २०८३", "Newari",
                "Bhaktapur's most spectacular festival, marking the Nepali New Year. A massive wooden chariot carrying the wrathful god Bhairava is hauled through narrow streets in a dramatic tug-of-war between the city's upper and lower halves, and a towering ceremonial pole (lingo) is raised and toppled to mark the year's turn.",
                "बिस्केट जात्रा भक्तपुरको सबैभन्दा भव्य पर्व हो, जसले नेपाली नयाँ वर्षको सुरुवात गर्छ। भैरवनाथको विशाल रथलाई सहरका माथिल्लो र तल्लो टोलबीचको रस्साकस्सीमा तानिन्छ, र अग्लो लिंगो ठड्याएर ढालेर वर्ष परिवर्तनको संकेत गरिन्छ।",
                "Bhaktapur", R.drawable.fest_bisket, Color.parseColor("#BC6C25")));

        f.add(new Festival("Nepali New Year", "नेपाली नयाँ वर्ष", "2026-04-14", "वैशाख १, २०८३", "National",
                "Nawa Barsha — the first day of Bikram Sambat 2083. Celebrated nationwide with family gatherings, picnics, concerts and cultural programs. In Madhyapur Thimi, the vivid Sindur Jatra fills the streets with orange vermilion powder, chariots of deities and traditional Dhimay drumming.",
                "नयाँ वर्ष विक्रम सम्वत् २०८३ को पहिलो दिन हो। देशभर पारिवारिक भेटघाट, वनभोज र सांस्कृतिक कार्यक्रमहरूद्वारा मनाइन्छ। मध्यपुर थिमिमा सिन्दूर जात्राले सडकहरू सुन्तला रंगको अबिर, देवताका खटहरू र धिमे बाजाले भरिन्छन्।",
                "Nationwide, Madhyapur Thimi", R.drawable.fest_new_year, Color.parseColor("#457B9D")));

        f.add(new Festival("Buddha Jayanti", "बुद्ध जयन्ती", "2026-05-01", "वैशाख, २०८३", "Buddhist",
                "The thrice-blessed day commemorating the birth, enlightenment and passing of Gautama Buddha — born in Lumbini, Nepal. Boudhanath and Swayambhunath glow with thousands of butter lamps, prayer flags flutter overhead, and processions of monks and pilgrims circle the great stupas.",
                "बुद्ध जयन्ती गौतम बुद्धको जन्म, बुद्धत्व प्राप्ति र महापरिनिर्वाणको त्रिस्मृति दिवस हो। बुद्धको जन्मस्थल लुम्बिनी नेपालमै पर्छ। बौद्धनाथ र स्वयम्भूमा हजारौं बत्ती बालिन्छन्, ध्वजापताका फहराइन्छन् र भिक्षु तथा तीर्थयात्रीहरू स्तूप परिक्रमा गर्छन्।",
                "Boudhanath, Swayambhu, Lumbini", R.drawable.fest_buddha, Color.parseColor("#F4A261")));

        f.add(new Festival("Mata Tirtha Aunsi", "आमाको मुख हेर्ने दिन", "2026-05-16", "वैशाख, २०८३", "Hindu",
                "Nepali Mother's Day. Children honor their mothers with gifts, sweets and heartfelt blessings. Those whose mothers have passed away make a pilgrimage to the sacred Mata Tirtha ponds near Kathmandu, where looking into the water is said to show the departed mother's face.",
                "आमाको मुख हेर्ने दिनमा सन्तानले आमालाई उपहार, मिठाई र आशीर्वाद अर्पण गर्छन्। आमा बितेकाहरू काठमाडौं नजिकको माता तीर्थ पोखरीमा गई स्नान र श्राद्ध गर्छन् — पोखरीको पानीमा दिवंगत आमाको मुहार देखिन्छ भन्ने जनविश्वास छ।",
                "Mata Tirtha, Kathmandu", R.drawable.fest_mata_tirtha, Color.parseColor("#E76F51")));

        f.add(new Festival("Tiji Festival", "तिजी पर्व", "2026-05-16", "जेठ, २०८३", "Buddhist",
                "A three-day Tibetan Buddhist ritual in Lo Manthang, the 600-year-old walled capital of Upper Mustang. Monks in elaborate costumes perform Cham masked dances retelling the victory of the deity Dorje Jono over a demon that brought drought — a prayer for water and peace in one of Earth's most dramatic landscapes.",
                "तिजी पर्व माथिल्लो मुस्ताङको प्राचीन राजधानी लोमान्थाङमा तीन दिनसम्म मनाइने तिब्बती बौद्ध अनुष्ठान हो। भिक्षुहरूले छाम नृत्यमार्फत दोर्जे जोनो देवताले खडेरी ल्याउने राक्षसमाथि विजय प्राप्त गरेको कथा प्रस्तुत गर्छन्।",
                "Lo Manthang, Upper Mustang", R.drawable.fest_tiji, Color.parseColor("#2A9D8F")));

        f.add(new Festival("Rato Machindranath Jatra", "रातो मछिन्द्रनाथ जात्रा", "2026-05-25", "जेठ, २०८३", "Newari",
                "One of Nepal's longest chariot festivals. A towering 20-meter chariot carrying Rato Machindranath — the red god of rain and harvest — is hauled for weeks through Patan, Bungmati and surrounding towns, seeking blessings for a bountiful monsoon.",
                "रातो मछिन्द्रनाथ जात्रा नेपालको सबैभन्दा लामो रथयात्रा मध्ये एक हो। वर्षा र अन्नबालीका देवता रातो मछिन्द्रनाथको करिब २० मिटर अग्लो रथलाई हप्तौंसम्म पाटन, बुङमती लगायतका ठाउँमा तानिन्छ, राम्रो मनसुनको कामना गर्दै।",
                "Patan, Lalitpur, Bungmati", R.drawable.fest_machindranath, Color.parseColor("#E63946")));

        f.add(new Festival("Ubhauli Parva", "उभौली पर्व", "2026-05-31", "जेठ, २०८३", "Kirat",
                "The Kirat community's great planting festival, marking the ritual upward migration toward higher lands for summer. Communities gather to perform the Sakela (Chandi) dance in large circles, honoring nature and praying for a good harvest.",
                "उभौली पर्व किरात समुदायको महान् रोपाइँ पर्व हो, जसले गर्मीका लागि उँभो (माथिल्लो भेग) सर्ने परम्परालाई जनाउँछ। समुदायहरू ठूला वृत्तमा साकेला (चण्डी) नाच नाच्दै प्रकृतिको पूजा र राम्रो बालीको कामना गर्छन्।",
                "Tundikhel, eastern Nepal", R.drawable.fest_ubhauli, Color.parseColor("#606C38")));

        f.add(new Festival("Gai Jatra", "गाईजात्रा", "2026-08-29", "भदौ, २०८३", "Newari",
                "The festival of cows — a Newari procession where families who lost a loved one in the past year send a cow (or a child dressed as one) to lead the departed soul to heaven. Uniquely, the day blends mourning with satire: comedy, costumes and social commentary fill the streets, turning grief into shared laughter.",
                "गाईजात्रा नेवार समुदायको पर्व हो, जसमा गत वर्ष परिवारका सदस्य गुमाएकाहरूले गाई (वा गाईको भेषमा बालक) पठाएर दिवंगत आत्मालाई स्वर्गको बाटो देखाउँछन्। शोकसँगै हास्यव्यंग्य, प्रहसन र सामाजिक टिप्पणीले सडक भरिन्छन् — शोकलाई हाँसोमा बदल्ने अनौठो पर्व।",
                "Kathmandu Valley, Bhaktapur", R.drawable.fest_gai_jatra, Color.parseColor("#BC6C25")));

        f.add(new Festival("Krishna Janmashtami", "कृष्ण जन्माष्टमी", "2026-09-04", "भदौ, २०८३", "Hindu",
                "Celebrates the midnight birth of Lord Krishna. Devotees fast through the day and keep vigil until midnight with bhajans and dances. The 17th-century Krishna Mandir in Patan Durbar Square becomes the center of celebration, ringed by thousands of lamps and devotees.",
                "कृष्ण जन्माष्टमी भगवान कृष्णको मध्यरात्रिको जन्मोत्सव हो। भक्तजनहरू दिनभर व्रत बसी मध्यरातसम्म भजनकीर्तन गर्छन्। पाटन दरबार स्क्वायरको ऐतिहासिक कृष्ण मन्दिर हजारौं दीप र भक्तहरूले घेरिन्छ।",
                "Krishna Mandir, Patan", R.drawable.fest_janmashtami, Color.parseColor("#457B9D")));

        f.add(new Festival("Teej", "हरितालिका तीज", "2026-09-14", "भदौ, २०८३", "Hindu",
                "The great festival of Nepali women. Dressed in brilliant red saris and green bangles, women fast for the long life and well-being of their husbands and families, and for happiness in marriage. Pashupatinath overflows with singing and dancing — Teej songs famously blend devotion with witty, honest commentary on women's lives.",
                "तीज नेपाली महिलाहरूको महान् पर्व हो। रातो सारी र हरिया चुरामा सजिएका महिलाहरू पति र परिवारको दीर्घायु तथा सुखको कामना गर्दै निराहार व्रत बस्छन्। पशुपतिनाथ परिसर गीत र नृत्यले गुञ्जायमान हुन्छ — तीजका गीतहरूमा भक्ति र महिला जीवनका यथार्थ दुवै पोखिन्छन्।",
                "Pashupatinath, across Nepal", R.drawable.fest_teej, Color.parseColor("#D62828")));

        f.add(new Festival("Indra Jatra", "इन्द्र जात्रा", "2026-10-04", "असोज, २०८३", "Newari",
                "Kathmandu's grandest street festival, honoring Indra, king of the gods and lord of rain. For eight days Basantapur pulses with masked Lakhe dances, the towering Yosin pole, displays of the great Bhairava masks — and the awe-inspiring chariot procession of the Kumari, Nepal's living goddess, through the old city.",
                "इन्द्र जात्रा काठमाडौंको सबैभन्दा भव्य सडक पर्व हो, जुन वर्षाका देवता इन्द्रको सम्मानमा मनाइन्छ। आठ दिनसम्म बसन्तपुरमा लाखे नाच, योसिं ठड्याउने विधि, भैरवका विशाल मुकुण्डा प्रदर्शन र जीवित देवी कुमारीको रथयात्राले पुरानो सहर जीवन्त बन्छ।",
                "Basantapur, Kathmandu Durbar Square", R.drawable.fest_indra_jatra, Color.parseColor("#457B9D")));

        f.add(new Festival("Ghatasthapana (Dashain Begins)", "घटस्थापना", "2026-10-11", "असोज, २०८३", "Hindu",
                "The first day of Dashain — Nepal's longest, biggest festival, celebrating the goddess Durga's victory over the demon Mahishasura. A sacred vessel (ghata) is installed and barley seeds are sown in every home; over the coming days they sprout into golden-yellow jamara, worn later as a blessing during Tika.",
                "घटस्थापना दशैंको पहिलो दिन हो — नेपालको सबैभन्दा लामो र ठूलो चाड, जसले देवी दुर्गाले महिषासुर राक्षसमाथि विजय प्राप्त गरेको उत्सव मनाउँछ। घरघरमा घट (कलश) स्थापना गरी जौ छरिन्छ, जुन उम्रेर सुनौलो जमरा बन्छ र टीकाका दिन आशीर्वादसँगै लगाइन्छ।",
                "Nationwide", R.drawable.fest_ghatasthapana, Color.parseColor("#E63946")));

        f.add(new Festival("Fulpati (Dashain)", "फूलपाती", "2026-10-17", "असोज, २०८३", "Hindu",
                "The seventh day of Dashain. Sacred offerings of flowers, leaves, sugarcane and jamara — the phulpati — are carried in a grand procession from the ancestral Gorkha Durbar to Hanuman Dhoka in Kathmandu, escorted by government officials, brass bands and the Nepal Army.",
                "फूलपाती दशैंको सातौं दिन हो। फूल, पात, उखु र जमराको पवित्र फूलपाती गोरखा दरबारबाट काठमाडौंको हनुमानढोकासम्म भव्य शोभायात्रामा ल्याइन्छ, जसमा सरकारी अधिकारी, बाजागाजा र नेपाली सेना सहभागी हुन्छन्।",
                "Hanuman Dhoka, Kathmandu", R.drawable.fest_fulpati, Color.parseColor("#D62828")));

        f.add(new Festival("Bijaya Dashami (Tika)", "विजया दशमी", "2026-10-21", "असोज, २०८३", "Hindu",
                "The main day of Dashain and the emotional heart of the Nepali year. Elders place red tika (rice, yogurt and vermilion) and golden jamara on the foreheads of younger family members, offering blessings and dakshina. Families travel across the country — and the world — to be together, celebrating good's triumph over evil.",
                "विजया दशमी दशैंको मुख्य दिन र नेपाली वर्षको भावनात्मक केन्द्र हो। मान्यजनले भाइबहिनी तथा सन्तानको निधारमा रातो टीका र जमरा लगाई आशीर्वाद र दक्षिणा दिन्छन्। परिवार भेट्न मानिसहरू देश-विदेशबाट घर फर्किन्छन् — असत्यमाथि सत्यको विजयको उत्सव।",
                "Every home in Nepal", R.drawable.fest_dashami, Color.parseColor("#C1121F")));

        f.add(new Festival("Kag Tihar (Tihar Begins)", "काग तिहार", "2026-11-07", "कात्तिक, २०८३", "Hindu",
                "The first day of Tihar, dedicated to crows — the messengers of Yama, the god of death. Households offer rice, sweets and grains on rooftops and courtyards to honor the crows and ward off grief and bad news in the year ahead.",
                "काग तिहार तिहारको पहिलो दिन हो, जुन यमराजका दूत मानिने कागलाई समर्पित छ। घरघरमा छत र आँगनमा भात, मिठाई र अन्न राखेर कागलाई खुवाइन्छ, ताकि आउँदो वर्ष शोक र नराम्रा खबरबाट टाढा रहियोस्।",
                "Nationwide", R.drawable.fest_kag_tihar, Color.parseColor("#606C38")));

        f.add(new Festival("Kukur Tihar", "कुकुर तिहार", "2026-11-08", "कात्तिक, २०८३", "Hindu",
                "The second day of Tihar, and one of Nepal's most beloved traditions worldwide: the day of the dogs. Every dog — family pets, police dogs and street dogs alike — receives a red tika, a marigold garland and a feast, honoring their loyalty, friendship and their role as guardians of the gates of the afterlife.",
                "कुकुर तिहार तिहारको दोस्रो दिन हो — कुकुरहरूको दिन। घरपालुवा, प्रहरी र सडकका सबै कुकुरलाई टीका, सयपत्रीको माला र मीठो भोजन दिइन्छ। यसले कुकुरको वफादारी, मित्रता र परलोकको ढोका रक्षकको भूमिकालाई सम्मान गर्छ।",
                "Nationwide", R.drawable.fest_kukur_tihar, Color.parseColor("#F4A261")));

        f.add(new Festival("Laxmi Puja (Tihar)", "लक्ष्मी पूजा", "2026-11-09", "कात्तिक, २०८३", "Hindu",
                "The most luminous night of the Nepali year. Homes are scrubbed clean and decorated with rangoli, marigold garlands and rows of oil lamps to welcome Laxmi, goddess of wealth. Girls go door to door singing Bhailo songs, and the whole Kathmandu Valley glows end to end — Tihar at its most magical.",
                "लक्ष्मी पूजा नेपाली वर्षको सबैभन्दा उज्यालो रात हो। घरहरू सफा गरी रंगोली, सयपत्रीका माला र दियोका लहरले सजाइन्छ, धनकी देवी लक्ष्मीलाई भित्र्याउन। केटीहरू भैलो गाउँदै घरघर पुग्छन् र सिंगो काठमाडौं उपत्यका झिलिमिली बन्छ।",
                "Nationwide, Thamel, Patan", R.drawable.fest_laxmi_puja, Color.parseColor("#FFB703")));

        f.add(new Festival("Govardhan Puja / Mha Puja", "गोवर्धन पूजा / म्ह पूजा", "2026-11-10", "कात्तिक, २०८३", "Hindu",
                "The fourth day of Tihar honors oxen and Govardhan hill, while the Newar community celebrates Mha Puja — the worship of one's own self and body — marking the start of Nepal Sambat, the Newari New Year. Elaborate mandalas are drawn for each family member in a ritual of self-purification and renewal.",
                "तिहारको चौथो दिन गोरु र गोवर्धन पर्वतको पूजा गरिन्छ भने नेवार समुदायले म्ह पूजा मनाउँछ — आफ्नै शरीर र आत्माको पूजा, जुन नेपाल सम्वत् (नेवारी नयाँ वर्ष) को सुरुवात पनि हो। परिवारका प्रत्येक सदस्यका लागि मण्डल बनाई आत्मशुद्धिको विधि गरिन्छ।",
                "Kathmandu Valley", R.drawable.fest_mha_puja, Color.parseColor("#9D4EDD")));

        f.add(new Festival("Bhai Tika (Tihar Ends)", "भाइटीका", "2026-11-11", "कात्तिक, २०८३", "Hindu",
                "The final and most tender day of Tihar. Sisters apply a seven-colored tika to their brothers' foreheads, circle them with mustard oil and marigolds, and offer sweets and walnuts — praying for their long life, while brothers give gifts and pledge lifelong protection in return.",
                "भाइटीका तिहारको अन्तिम र सबैभन्दा भावुक दिन हो। दिदीबहिनीले दाजुभाइको निधारमा सप्तरंगी टीका लगाई, तोरीको तेल र सयपत्रीले घेरा हाली, मिठाई र ओखर दिँदै दीर्घायुको कामना गर्छन्। दाजुभाइले उपहारसँगै जीवनभर रक्षा गर्ने वचन दिन्छन्।",
                "Every home in Nepal", R.drawable.fest_bhai_tika, Color.parseColor("#FB8500")));

        f.add(new Festival("Chhath Parva", "छठ पर्व", "2026-11-15", "कात्तिक, २०८३", "Hindu",
                "A four-day festival of rigorous devotion to Surya, the Sun God, and Chhathi Maiya. Devotees — mostly women — observe a 36-hour waterless fast and offer arghya to the setting and rising sun while standing waist-deep in rivers and ponds. Janakpur and the Terai riverbanks transform into oceans of lamps, fruit offerings and song.",
                "छठ सूर्य भगवान र छठी मैयाको आराधनामा चार दिनसम्म मनाइने कठोर व्रतको पर्व हो। व्रतालुहरू ३६ घण्टा निर्जला व्रत बसी नदी र पोखरीमा उभिएर अस्ताउँदो र उदाउँदो सूर्यलाई अर्घ्य दिन्छन्। जनकपुर लगायत तराईका घाटहरू दीप, फलफूल र छठ गीतले भरिन्छन्।",
                "Janakpur, Terai, riverbanks", R.drawable.fest_chhath, Color.parseColor("#E76F51")));

        f.add(new Festival("Bibaha Panchami", "विवाह पञ्चमी", "2026-11-24", "मंसिर, २०८३", "Hindu",
                "Commemorates the divine wedding of Lord Ram and Goddess Sita at the very place it is said to have happened — Janakpur. A week of processions, music and rituals culminates in a grand reenactment of the celestial marriage at the Janaki Temple, drawing pilgrims from across Nepal and India.",
                "विवाह पञ्चमीले भगवान राम र देवी सीताको दिव्य विवाहको स्मरण गराउँछ — त्यही ठाउँमा जहाँ यो भएको मानिन्छ, जनकपुरमा। साताभरका शोभायात्रा, संगीत र विधिपछि जानकी मन्दिरमा भव्य विवाह पुनःअभिनय गरिन्छ, जसमा नेपाल र भारतबाट तीर्थयात्री आउँछन्।",
                "Janakpur", R.drawable.fest_bibaha, Color.parseColor("#9D4EDD")));

        f.add(new Festival("Yomari Punhi", "योमरी पुन्ही", "2026-12-03", "मंसिर, २०८३", "Newari",
                "The Newar harvest full-moon festival celebrating the season's new rice. Families steam yomari — fish-tailed dumplings of rice flour filled with chaku (molasses) and sesame — and offer them to Annapurna, the goddess of grains, in thanks for the harvest.",
                "योमरी पुन्ही नेवार समुदायको धान भित्र्याएपछि मनाइने पूर्णिमा पर्व हो। चामलको पिठोमा चाकु र तिल भरेर बनाइने योमरी पकाई अन्नकी देवी अन्नपूर्णलाई चढाइन्छ र फसलका लागि कृतज्ञता प्रकट गरिन्छ।",
                "Kathmandu Valley", R.drawable.fest_yomari, Color.parseColor("#BC6C25")));

        f.add(new Festival("Tamu Lhosar", "तमु ल्होसार", "2026-12-30", "पुस, २०८३", "Buddhist",
                "The Gurung (Tamu) community's New Year, closing out the calendar year in style. Kathmandu's Tundikhel and Pokhara's lakeside fill with people in traditional dress performing the community's distinctive circular dances, sharing feasts and welcoming the new year with music and blessings.",
                "तमु ल्होसार गुरुङ (तमु) समुदायको नयाँ वर्ष हो। काठमाडौंको टुँडिखेल र पोखराको तालबाराहीमा परम्परागत पोशाकमा सजिएका मानिसहरू विशिष्ट गोलाकार नृत्य गर्दै, भोज बाँड्दै संगीत र आशीर्वादसहित नयाँ वर्ष भित्र्याउँछन्।",
                "Tundikhel, Pokhara", R.drawable.fest_tamu_lhosar, Color.parseColor("#2A9D8F")));


        f.add(new Festival("Basanta Panchami", "वसन्त पञ्चमी / सरस्वती पूजा", "2026-01-23", "माघ, २०८२", "Hindu",
                "The first day of spring, dedicated to Saraswati, goddess of knowledge, music and arts. Students flock to Saraswati temples to have their books and pens blessed, and small children are given their very first letters to write on this auspicious day.",
                "वसन्त पञ्चमी वसन्त ऋतुको पहिलो दिन हो, जुन विद्या र कलाकी देवी सरस्वतीलाई समर्पित छ। विद्यार्थीहरू किताब-कलम पूजा गर्न मन्दिर पुग्छन् र साना बालबालिकालाई यसै दिन पहिलो अक्षर लेखाइन्छ।",
                "Saraswati temples, nationwide", R.drawable.fest_saraswati, Color.parseColor("#F9C74F")));

        f.add(new Festival("Gyalpo Lhosar", "ग्याल्पो ल्होसार", "2026-03-09", "फागुन, २०८२", "Buddhist",
                "The New Year of the Sherpa and Tibetan communities, celebrated for two weeks with monastery prayers, family feasts, masked dances and the exchange of khada scarves. Boudhanath becomes the vibrant center of the celebrations.",
                "ग्याल्पो ल्होसार शेर्पा तथा तिब्बती समुदायको नयाँ वर्ष हो। गुम्बामा प्रार्थना, पारिवारिक भोज, नकाब नृत्य र खादा आदानप्रदान गरेर दुई हप्तासम्म मनाइन्छ। बौद्धनाथ उत्सवको मुख्य केन्द्र बन्छ।",
                "Boudhanath, Solukhumbu", R.drawable.fest_gyalpo, Color.parseColor("#577590")));

        f.add(new Festival("Chaite Dashain", "चैते दशैं", "2026-03-26", "चैत, २०८२", "Hindu",
                "The 'small Dashain' of spring, honoring Goddess Durga six months before the great autumn festival. Devotees visit shakti temples and some households perform tika in miniature of the main celebration.",
                "चैते दशैं वसन्तमा मनाइने सानो दशैं हो, जसमा देवी दुर्गाको पूजा गरिन्छ। भक्तजन शक्तिपीठ दर्शन गर्छन् र कतिपय घरमा सानो स्तरमा टीका पनि लगाइन्छ।",
                "Shakti temples, nationwide", R.drawable.fest_chaite, Color.parseColor("#F3722C")));

        f.add(new Festival("Saune Sankranti", "साउने संक्रान्ति", "2026-07-16", "साउन १, २०८३", "Hindu",
                "The first day of Shrawan, opening the holiest month of the monsoon. Women wear green and yellow bangles and mehendi, devotees of Shiva fast on Mondays, and farmers celebrate the season of planting rains.",
                "साउने संक्रान्ति साउन महिनाको पहिलो दिन हो। महिलाहरू हरिया चुरा र मेहन्दी लगाउँछन्, शिवभक्तहरू सोमबार व्रत बस्छन् र किसानहरू रोपाइँको वर्षालाई उत्सवका रूपमा मनाउँछन्।",
                "Nationwide", R.drawable.fest_saune, Color.parseColor("#43AA8B")));

        f.add(new Festival("Guru Purnima", "गुरु पूर्णिमा", "2026-07-29", "साउन, २०८३", "Hindu",
                "The full-moon day dedicated to teachers and gurus. Students honor their teachers with flowers, tika and gifts, and Buddhists mark the day of Buddha's first sermon at Sarnath.",
                "गुरु पूर्णिमा गुरुहरूप्रति श्रद्धा अर्पण गर्ने पूर्णिमाको दिन हो। विद्यार्थीहरूले गुरुलाई फूल, टीका र उपहार चढाउँछन् भने बौद्धहरूले बुद्धको प्रथम उपदेशको स्मरण गर्छन्।",
                "Schools, monasteries, nationwide", R.drawable.fest_guru_purnima, Color.parseColor("#90BE6D")));

        f.add(new Festival("Gathemangal (Ghantakarna)", "गथेमंगल / घण्टाकर्ण", "2026-08-12", "साउन, २०८३", "Newari",
                "The Newar festival of chasing away the demon Ghantakarna. Effigies of the demon are erected at crossroads and dramatically burned or dragged away at dusk, cleansing the town of evil spirits for the festival season ahead.",
                "गथेमंगल नेवार समुदायले घण्टाकर्ण राक्षस धपाउने पर्व हो। चोकचोकमा राक्षसको पुत्ला ठड्याई साँझ जलाइन्छ वा घिसारिन्छ, जसले आगामी चाडपर्वका लागि सहरलाई दुष्ट आत्माबाट शुद्ध पार्छ।",
                "Kathmandu Valley", R.drawable.fest_gathemangal, Color.parseColor("#4D4861")));

        f.add(new Festival("Nag Panchami", "नाग पञ्चमी", "2026-08-18", "साउन, २०८३", "Hindu",
                "The day of the serpent deities. Households paste images of nagas above their doorways with cow-dung and offer milk, honey and rice to snake gods, seeking protection from lightning, snakebite and water troubles.",
                "नाग पञ्चमीमा घरका ढोकामाथि नागको चित्र टाँसेर दूध, मह र अक्षता चढाइन्छ। नाग देवताको पूजाले चट्याङ, सर्पदंश र जलसम्बन्धी समस्याबाट रक्षा हुने विश्वास छ।",
                "Nag Pokhari, nationwide", R.drawable.fest_nag, Color.parseColor("#2D6A4F")));

        f.add(new Festival("Janai Purnima / Rakshya Bandhan", "जनै पूर्णिमा / रक्षाबन्धन", "2026-08-28", "भदौ, २०८३", "Hindu",
                "The sacred thread festival. Hindu men renew their janai, everyone ties a protective rakshya doro around the wrist, and pilgrims trek to the holy lake of Gosaikunda. In the Terai sisters tie rakhi on their brothers' wrists.",
                "जनै पूर्णिमामा पुरुषहरूले जनै फेर्छन् र सबैले हातमा रक्षाबन्धनको डोरो बाँध्छन्। तीर्थयात्रीहरू गोसाइँकुण्डसम्म पुग्छन् भने तराईमा दिदीबहिनीले दाजुभाइलाई राखी बाँध्छन्।",
                "Gosaikunda, Kumbheshwar, nationwide", R.drawable.fest_janai, Color.parseColor("#F94144")));

        f.add(new Festival("Kushe Aunsi (Father's Day)", "कुशे औंसी / बुवाको मुख हेर्ने दिन", "2026-09-11", "भदौ, २०८३", "Hindu",
                "Nepali Father's Day. Children honor their fathers with gifts, sweets and deep respect, while those whose fathers have passed away perform shraddha rituals at Gokarna. Sacred kush grass is also brought home on this day.",
                "कुशे औंसीमा सन्तानले बुवालाई उपहार र मिठाईसहित सम्मान गर्छन्। बुवा बितेकाहरू गोकर्णमा श्राद्ध गर्छन्। यसै दिन घरमा पवित्र कुश पनि भित्र्याइन्छ।",
                "Gokarna, nationwide", R.drawable.fest_kushe, Color.parseColor("#577590")));

        f.add(new Festival("Rishi Panchami", "ऋषि पञ्चमी", "2026-09-16", "भदौ, २०८३", "Hindu",
                "The closing day of the Teej festival, honoring the seven great sages (Sapta Rishi). Women take ritual baths with datiwan leaves at riverbanks and perform puja seeking purification and blessings.",
                "ऋषि पञ्चमी तीजको अन्तिम दिन हो, जसमा सप्तऋषिको पूजा गरिन्छ। महिलाहरू नदी किनारमा दतिवनले स्नान गरी शुद्धि र आशीर्वादका लागि पूजा गर्छन्।",
                "Riverbanks, nationwide", R.drawable.fest_rishi, Color.parseColor("#4D908E")));

        f.add(new Festival("Jitiya Parva", "जितिया पर्व", "2026-10-03", "असोज, २०८३", "Hindu",
                "A rigorous fasting festival of Terai mothers, observed for the long life and prosperity of their children. Women keep a strict nirjala fast — without even water — through day and night, breaking it only after rituals the next morning.",
                "जितिया तराईका आमाहरूले सन्तानको दीर्घायु र समृद्धिका लागि राख्ने कठोर व्रत हो। महिलाहरू रातदिन निर्जला व्रत बसी भोलिपल्ट बिहान विधिपूर्वक पारण गर्छन्।",
                "Terai, Janakpur, Mithila region", R.drawable.fest_jitiya, Color.parseColor("#B5838D")));

        f.add(new Festival("Maha Ashtami (Dashain)", "महाअष्टमी", "2026-10-19", "असोज, २०८३", "Hindu",
                "The eighth and fiercest day of Dashain, dedicated to Kali, the most powerful form of Durga. Temples across the country receive offerings through the night of Kal Ratri, and families hold special feasts.",
                "महाअष्टमी दशैंको आठौं दिन हो, जुन दुर्गाको उग्र रूप कालीलाई समर्पित छ। कालरात्रिभर देशभरका शक्तिपीठहरूमा विशेष पूजा हुन्छ र घरघरमा भोज चल्छ।",
                "Shakti temples, nationwide", R.drawable.fest_ashtami, Color.parseColor("#9D0208")));

        f.add(new Festival("Maha Nawami (Dashain)", "महानवमी", "2026-10-20", "असोज, २०८३", "Hindu",
                "The ninth day of Dashain. Vehicles, tools and instruments of trade are blessed in honor of Vishwakarma, and the Taleju temple at Kathmandu Durbar Square — closed all year — opens its doors to the public for this single day.",
                "महानवमीमा विश्वकर्माको सम्मानमा सवारीसाधन र औजारहरूको पूजा गरिन्छ। वर्षभर बन्द रहने हनुमानढोकाको तलेजु मन्दिर यसै दिन मात्र सर्वसाधारणका लागि खुल्छ।",
                "Taleju Temple, nationwide", R.drawable.fest_nawami, Color.parseColor("#DC2F02")));

        f.add(new Festival("Kojagrat Purnima", "कोजाग्रत पूर्णिमा", "2026-10-25", "कात्तिक, २०८३", "Hindu",
                "The full moon that closes Dashain. Goddess Laxmi is believed to descend at night asking 'Ko jagrat?' — who is awake? — blessing those keeping vigil with prosperity. Families stay up playing cards and sharing food under the moonlight.",
                "कोजाग्रत पूर्णिमाले दशैंको समापन गर्छ। यस रात देवी लक्ष्मी 'को जाग्रत?' भन्दै ओर्लिने र जागा रहनेलाई समृद्धिको वरदान दिने जनविश्वास छ। परिवारजन जुनेली रातमा तास खेल्दै जाग्राम बस्छन्।",
                "Nationwide", R.drawable.fest_kojagrat, Color.parseColor("#FFBA08")));

        f.add(new Festival("Mani Rimdu", "मणि रिम्दु", "2026-11-19", "मंसिर, २०८३", "Buddhist",
                "The great festival of Tengboche Monastery in the Everest region. Over nineteen days of rituals culminate in three public days of spectacular Cham masked dances by the monks, celebrating Buddhism's victory over the old Bon religion.",
                "मणि रिम्दु सगरमाथा क्षेत्रको तेङबोचे गुम्बाको प्रमुख पर्व हो। उन्नाइस दिनका विधिपछि भिक्षुहरूले तीन दिनसम्म सार्वजनिक रूपमा भव्य छाम नृत्य प्रस्तुत गर्छन्।",
                "Tengboche Monastery, Khumbu", R.drawable.fest_mani_rimdu, Color.parseColor("#277DA1")));

        f.add(new Festival("Udhauli Parva", "उधौली पर्व", "2026-12-23", "पुस, २०८३", "Kirat",
                "The Kirat harvest festival marking the ritual downward migration to warmer lowlands for winter. Communities thank nature for the harvest with the great Sakela dance, drumming and feasts — the mirror of springtime Ubhauli.",
                "उधौली किरात समुदायको फसल पर्व हो, जसले जाडोका लागि उँधो (बेंसी) झर्ने परम्परालाई जनाउँछ। साकेला नाच, ढोल र भोजसहित प्रकृतिप्रति कृतज्ञता प्रकट गरिन्छ — यो वसन्तको उभौलीको जोडी पर्व हो।",
                "Eastern Nepal, Tundikhel", R.drawable.fest_udhauli, Color.parseColor("#BC6C25")));

        f.add(new Festival("Christmas", "क्रिसमस", "2026-12-25", "पुस, २०८३", "National",
                "A national holiday in Nepal celebrating the birth of Jesus Christ. Churches hold midnight services, Thamel and the malls glitter with lights and trees, and people of all faiths join the festive cheer.",
                "क्रिसमस नेपालमा सार्वजनिक बिदाको दिन हो, जसले येशू ख्रीष्टको जन्मोत्सव मनाउँछ। चर्चहरूमा मध्यरात्रि प्रार्थना हुन्छ भने ठमेल र बजारहरू झिलिमिली बत्ती र क्रिसमस ट्रीले सजिन्छन्।",
                "Churches, Thamel, nationwide", R.drawable.fest_christmas, Color.parseColor("#D62828")));

        f.add(new Festival("Swasthani Brata Katha Begins", "स्वस्थानी व्रत आरम्भ", "2026-01-03", "पुस, २०८२", "Hindu",
                "The month-long recitation of the Swasthani Katha begins on the full moon of Poush. Families read one chapter nightly, and devout women observe the famous month-long fast at Sali Nadi, Sankhu.",
                "पुस पूर्णिमादेखि एक महिनासम्म स्वस्थानी व्रतकथा वाचन सुरु हुन्छ। घरघरमा रातिपिच्छे एक अध्याय पढिन्छ भने साँखुको सालीनदीमा महिलाहरू महिना दिनको कठोर व्रत बस्छन्।",
                "Sali Nadi (Sankhu), nationwide", R.drawable.fest_swasthani, Color.parseColor("#C1121F")));

        f.add(new Festival("Prithvi Jayanti", "पृथ्वी जयन्ती", "2026-01-11", "पुस २७, २०८२", "National",
                "The birth anniversary of King Prithvi Narayan Shah, unifier of modern Nepal, observed as National Unity Day with ceremonies at his statues nationwide.",
                "आधुनिक नेपालका एकीकरणकर्ता पृथ्वीनारायण शाहको जन्मजयन्ती राष्ट्रिय एकता दिवसका रूपमा मनाइन्छ।",
                "Nationwide", R.drawable.fest_prithvi, Color.parseColor("#577590")));

        f.add(new Festival("Martyrs' Day", "शहीद दिवस", "2026-01-30", "माघ, २०८२", "National",
                "Honors the martyrs who gave their lives for Nepal's democracy. Processions and tributes are held at Shahid Gate in Kathmandu and across the country.",
                "नेपालको प्रजातन्त्रका लागि प्राण अर्पण गर्ने शहीदहरूको सम्झनामा शहीद दिवस मनाइन्छ। काठमाडौंको शहीद गेट लगायत देशभर श्रद्धाञ्जली कार्यक्रम हुन्छन्।",
                "Shahid Gate, nationwide", R.drawable.fest_martyrs, Color.parseColor("#9D0208")));

        f.add(new Festival("Democracy Day", "प्रजातन्त्र दिवस", "2026-02-19", "फागुन ७, २०८२", "National",
                "Falgun 7 marks the end of the Rana regime in 2007 BS and the dawn of democracy in Nepal, celebrated with official ceremonies and parades.",
                "फागुन ७ ले वि.सं. २००७ मा राणा शासनको अन्त्य र नेपालमा प्रजातन्त्रको उदयलाई जनाउँछ। यस दिन औपचारिक समारोह र परेड आयोजना गरिन्छ।",
                "Tundikhel, nationwide", R.drawable.fest_democracy, Color.parseColor("#457B9D")));

        f.add(new Festival("International Women's Day", "अन्तर्राष्ट्रिय महिला दिवस", "2026-03-08", "फागुन, २०८२", "National",
                "March 8 is a public holiday for women in Nepal, marked with rallies, cultural programs and campaigns for equality.",
                "मार्च ८ नेपालमा महिलाहरूका लागि सार्वजनिक बिदाको दिन हो। समानताका लागि र्‍याली, सांस्कृतिक कार्यक्रम र अभियानहरू आयोजना गरिन्छन्।",
                "Nationwide", R.drawable.fest_womens_day, Color.parseColor("#9D4EDD")));

        f.add(new Festival("Eid al-Fitr", "ईद-उल-फित्र", "2026-03-20", "चैत, २०८२", "Islamic",
                "The joyous feast ending Ramadan's month of fasting. Nepal's Muslim community gathers for Eid prayers at mosques and Eidgahs, shares sewai and gives zakat to the needy. Date depends on the moon sighting.",
                "रमजानको एक महिनाको रोजापछि मनाइने खुसीको पर्व। नेपालका मुस्लिम समुदाय मस्जिद र ईदगाहमा नमाज पढ्छन्, सेवई बाँड्छन् र जकात दिन्छन्। मिति चन्द्र दर्शनमा निर्भर हुन्छ।",
                "Jame Masjid, Terai, nationwide", R.drawable.fest_eid_fitr, Color.parseColor("#2A9D8F")));

        f.add(new Festival("International Labour Day", "मजदुर दिवस", "2026-05-01", "वैशाख, २०८३", "National",
                "May 1 honors workers and their movement. A public holiday with rallies and programs by trade unions across Nepal.",
                "मे १ श्रमिकहरू र तिनको आन्दोलनको सम्मानमा मनाइन्छ। नेपालभर मजदुर संगठनहरूले र्‍याली र कार्यक्रम गर्छन्।",
                "Nationwide", R.drawable.fest_labour, Color.parseColor("#BC6C25")));

        f.add(new Festival("Eid al-Adha (Bakar Eid)", "बकर ईद", "2026-05-27", "जेठ, २०८३", "Islamic",
                "The festival of sacrifice commemorating Prophet Ibrahim's devotion. Prayers, qurbani and sharing of meat with family, neighbors and the poor. Date depends on the moon sighting.",
                "पैगम्बर इब्राहिमको समर्पणको सम्झनामा मनाइने कुर्बानीको पर्व। नमाज, कुर्बानी र मासु परिवार, छिमेकी र गरिबहरूसँग बाँड्ने चलन छ। मिति चन्द्र दर्शनमा निर्भर हुन्छ।",
                "Mosques nationwide", R.drawable.fest_eid_adha, Color.parseColor("#264653")));

        f.add(new Festival("Pancha Dan", "पञ्चदान", "2026-08-30", "भदौ, २०८३", "Buddhist",
                "The Newar Buddhist day of the five summer gifts. Households set out grains, salt and money, and lines of monks and priests walk the old towns of the Valley receiving alms.",
                "पञ्चदान नेवार बौद्धहरूको दान पर्व हो। घरघरमा अन्न, नुन र दक्षिणा राखिन्छ र भिक्षु तथा गुभाजुहरू उपत्यकाका पुराना टोल डुल्दै दान ग्रहण गर्छन्।",
                "Kathmandu Valley, Patan", R.drawable.fest_panchadan, Color.parseColor("#F4A261")));

        f.add(new Festival("Gaura Parva", "गौरा पर्व", "2026-09-17", "भदौ, २०८३", "Hindu",
                "The great festival of far-western Nepal honoring Goddess Gaura. Women fast and worship with biruda grains, and communities join hands for the soulful Deuda dance in large circles.",
                "सुदूरपश्चिमको प्रमुख पर्व गौरामा महिलाहरू व्रत बसी बिरुडाले गौरा देवीको पूजा गर्छन्। समुदायहरू हातेमालो गर्दै देउडा नाच्छन्।",
                "Far-western Nepal, Tundikhel", R.drawable.fest_gaura, Color.parseColor("#D62828")));

        f.add(new Festival("Constitution Day", "संविधान दिवस", "2026-09-19", "असोज ३, २०८३", "National",
                "Asoj 3 marks the promulgation of Nepal's constitution in 2072 BS through an elected Constituent Assembly, celebrated as National Day.",
                "असोज ३ मा वि.सं. २०७२ सालमा संविधानसभाबाट नेपालको संविधान जारी भएको सम्झनामा राष्ट्रिय दिवसका रूपमा संविधान दिवस मनाइन्छ।",
                "Nationwide", R.drawable.fest_constitution, Color.parseColor("#577590")));

        f.add(new Festival("Gai Tihar / Govardhan", "गाई तिहार", "2026-11-09", "कात्तिक, २०८३", "Hindu",
                "On the morning of Laxmi Puja day, the cow — embodiment of Laxmi herself — is worshipped with tika, garlands and the season's best fodder.",
                "लक्ष्मीपूजाकै दिन बिहान लक्ष्मीकी प्रतीक गाईलाई टीका, माला र मीठो आहारले पूजा गरिन्छ।",
                "Nationwide", R.drawable.fest_gai_tihar, Color.parseColor("#F9C74F")));

        f.add(new Festival("Bala Chaturdashi", "बाला चतुर्दशी", "2026-12-08", "मंसिर, २०८३", "Hindu",
                "Families keep an all-night lamp vigil at Pashupatinath for departed loved ones, then at dawn scatter shatbij — seven sacred grains — along the forest paths of Shleshmantak, sowing merit for the departed souls.",
                "दिवंगत आफन्तको सम्झनामा पशुपति क्षेत्रमा रातभर दीप जलाई जाग्राम बसिन्छ र बिहान श्लेष्मान्तक वनमा सतबीज छरिन्छ।",
                "Pashupatinath, Shleshmantak forest", R.drawable.fest_bala, Color.parseColor("#606C38")));

        java.util.Collections.sort(f, (a, b) -> a.getDate().compareTo(b.getDate()));
        return f;
    }
}
