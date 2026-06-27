package com.example.data

data class Slogan(
    val language: String, // "English", "ગુજરાતી", "हिन्दी"
    val text: String
)

data class PosterTemplate(
    val id: String,
    val title: String,
    val category: String, // "Festival", "Business", "Marketing", "National Day", "Religious", "Birthday", "Offer"
    val dateStr: String, // "2026-06-27" (specific) or "Every Day", "Monthly", etc.
    val dateLabel: String, // e.g., "June 27", "Diwali", etc.
    val description: String,
    val drawableResName: String, // Reference matching our generated drawables
    val slogans: List<Slogan>
)

object PosterCatalog {
    val templates = listOf(
        PosterTemplate(
            id = "today_launch",
            title = "Poster365 Launch Celebration",
            category = "Special Day",
            dateStr = "2026-06-27",
            dateLabel = "June 27 (Today)",
            description = "Celebrate India's Smartest Calendar-Based Poster Maker App Launch!",
            drawableResName = "img_greeting_poster_1782576742228",
            slogans = listOf(
                Slogan("English", "Create professional business and festival posters in seconds with Poster365! Every Day Has a Design."),
                Slogan("ગુજરાતી", "Poster365 સાથે સેકન્ડોમાં પ્રોફેશનલ બિઝનેસ અને ફેસ્ટિવલ પોસ્ટર્સ બનાવો! દરેક દિવસની એક ડિઝાઇન."),
                Slogan("हिन्दी", "Poster365 के साथ सेकंडों में प्रोफेशनल बिजनेस और फेस्टिवल पोस्टर्स बनाएं! हर दिन का एक डिजाइन।")
            )
        ),
        PosterTemplate(
            id = "diwali_festival",
            title = "Diwali - Festival of Lights",
            category = "Festival",
            dateStr = "2026-11-05", // Diwali 2026 is around Nov 5
            dateLabel = "November 5 (Diwali)",
            description = "Sourced with radiant lamps, Rangoli, and sweets. Wish your brand partners a prosperous year ahead.",
            drawableResName = "img_diwali_poster_1782576673162",
            slogans = listOf(
                Slogan("English", "May the divine light of Diwali shine with peace, prosperity, and success for your business!"),
                Slogan("ગુજરાતી", "દિવાળીનો પાવન પ્રકાશ આપના વ્યવસાયમાં સુખ, શાંતિ અને સમૃદ્ધિ લાવે તેવી શુભકામનાઓ!"),
                Slogan("हिन्दी", "दीपावली का यह पावन त्योहार आपके जीवन और व्यापार में सुख, समृद्धि और सफलता लाए।")
            )
        ),
        PosterTemplate(
            id = "business_grow",
            title = "Business Success & Innovation",
            category = "Business",
            dateStr = "Every Day",
            dateLabel = "Any Day Marketing",
            description = "Promote your brand values, professional commitment, and market leadership.",
            drawableResName = "img_business_poster_1782576691428",
            slogans = listOf(
                Slogan("English", "Committed to delivering excellence and innovation. Partner with us for guaranteed growth!"),
                Slogan("ગુજરાતી", "શ્રેષ્ઠતા અને નવીનતા પ્રદાન કરવા માટે કટિબદ્ધ. ગેરંટીડ પ્રોગ્રેસ માટે અમારી સાથે જોડાઓ!"),
                Slogan("हिन्दी", "उत्कृष्टता और नवीनता प्रदान करने के लिए प्रतिबद्ध। निश्चित विकास के लिए हमारे साथ जुड़ें!")
            )
        ),
        PosterTemplate(
            id = "birthday_wishes",
            title = "Birthday & Anniversary Greetings",
            category = "Birthday",
            dateStr = "Every Day",
            dateLabel = "Any Birthday/Anniversary",
            description = "Send professional birthday or anniversary wishes customized with your company branding.",
            drawableResName = "img_birthday_poster_1782576707556",
            slogans = listOf(
                Slogan("English", "Wishing you a year filled with wonderful achievements, endless joy, and ultimate success!"),
                Slogan("ગુજરાતી", "તમને અદ્ભુત સિદ્ધિઓ, અસીમ આનંદ અને અપાર સફળતાથી ભરેલા વર્ષની હાર્દિક શુભેચ્છાઓ!"),
                Slogan("हिन्दी", "आपको अद्भुत उपलब्धियों, अनंत खुशियों और सर्वोच्च सफलता से भरे वर्ष की हार्दिक शुभकामनाएं!")
            )
        ),
        PosterTemplate(
            id = "shop_offer_mega",
            title = "Mega Sale & Exclusive Offers",
            category = "Offer",
            dateStr = "Every Day",
            dateLabel = "Shop Discounts",
            description = "Attract customers with special product discounts, limited time sales, and shop deals.",
            drawableResName = "img_offer_poster_1782576722671",
            slogans = listOf(
                Slogan("English", "Mega Discount Offer! Get the best quality products at unbeatable prices. Visit us today!"),
                Slogan("ગુજરાતી", "મેગા ડિસ્કાઉન્ટ ઓફર! અદ્ભુત કિંમતો પર શ્રેષ્ઠ ગુણવત્તાવાળી પ્રોડક્ટ્સ મેળવો. આજે જ મુલાકાત લો!"),
                Slogan("हिन्दी", "मेगा डिस्काउंट ऑफर! बेजोड़ कीमतों पर सर्वोत्तम गुणवत्ता वाले उत्पाद प्राप्त करें। आज ही पधारें!")
            )
        ),
        PosterTemplate(
            id = "gandhi_jayanti",
            title = "Gandhi Jayanti",
            category = "National Day",
            dateStr = "2026-10-02",
            dateLabel = "October 2",
            description = "Celebrate the birth of the Father of the Nation with peace and truth values.",
            drawableResName = "img_greeting_poster_1782576742228",
            slogans = listOf(
                Slogan("English", "Truth and non-violence are our greatest strengths. Tribute to Mahatma Gandhi on his birth anniversary."),
                Slogan("ગુજરાતી", "સત્ય અને અહિંસા એ આપણી સૌથી મોટી તાકાત છે. પૂજ્ય રાષ્ટ્રપિતા મહાત્મા ગાંધીજીની જન્મજયંતિ પર શત શત નમન."),
                Slogan("हिन्दी", "सत्य और अहिंसा हमारी सबसे बड़ी शक्ति हैं। राष्ट्रपिता महात्मा गांधी की जयंती पर उन्हें कोटि-कोटि नमन।")
            )
        ),
        PosterTemplate(
            id = "independence_day",
            title = "Independence Day of India",
            category = "National Day",
            dateStr = "2026-08-15",
            dateLabel = "August 15",
            description = "Celebrate freedom, progress, and unity in diversity with a tri-color brand poster.",
            drawableResName = "img_greeting_poster_1782576742228",
            slogans = listOf(
                Slogan("English", "Celebrating 80 years of freedom, pride, and progress. Happy Independence Day!"),
                Slogan("ગુજરાતી", "ગૌરવ, શૌર્ય અને સ્વતંત્રતાના પર્વની હાર્દિક શુભેચ્છાઓ. હેપી ઈન્ડિપેન્ડન્સ ડે!"),
                Slogan("हिन्दी", "स्वतंत्रता, गौरव और प्रगति के महापर्व की हार्दिक शुभकामनाएं। स्वतंत्रता दिवस मंगलमय हो!")
            )
        ),
        PosterTemplate(
            id = "brand_promo",
            title = "Brand Value Promotion",
            category = "Business",
            dateStr = "Every Day",
            dateLabel = "Any Day Marketing",
            description = "Highlight your reliability, premium service, and quality assurances.",
            drawableResName = "img_business_poster_1782576691428",
            slogans = listOf(
                Slogan("English", "Your trusted business partner for premium quality services. Making your business simpler."),
                Slogan("ગુજરાતી", "પ્રીમિયમ ગુણવત્તા સેવાઓ માટે તમારા વિશ્વાસુ વ્યવસાય ભાગીદાર. તમારા વ્યવસાયને વધુ સરળ બનાવીએ."),
                Slogan("हिन्दी", "प्रीमियम गुणवत्ता सेवाओं के लिए आपका भरोसेमंद बिजनेस पार्टनर। आपके व्यवसाय को बनाएं आसान।")
            )
        ),
        PosterTemplate(
            id = "ganesh_chaturthi",
            title = "Ganesh Chaturthi",
            category = "Religious",
            dateStr = "2026-09-14",
            dateLabel = "September 14",
            description = "Lord Ganesha brings wisdom, wealth, and prosperity to your business.",
            drawableResName = "img_diwali_poster_1782576673162",
            slogans = listOf(
                Slogan("English", "May Lord Ganesha remove all obstacles from your path and bless you with infinite growth!"),
                Slogan("ગુજરાતી", "ભગવાન ગણેશ આપના માર્ગમાંથી તમામ વિઘ્નો દૂર કરે અને આપના વ્યવસાયમાં સુખ-સમૃદ્ધિ આપે!"),
                Slogan("हिन्दी", "विघ्नहर्ता श्री गणेश आपके सभी विघ्नों को दूर कर आपको सुख, समृद्धि और सौभाग्य प्रदान करें।")
            )
        )
    )
    
    fun getPostersForDate(dateStr: String): List<PosterTemplate> {
        // Return posters that specifically match the date, plus the general 'Every Day' ones
        val specific = templates.filter { it.dateStr == dateStr }
        val generic = templates.filter { it.dateStr == "Every Day" }
        return (specific + generic).distinctBy { it.id }
    }

    fun searchPosters(query: String): List<PosterTemplate> {
        if (query.isBlank()) return templates
        val q = query.lowercase()
        return templates.filter {
            it.title.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.dateLabel.lowercase().contains(q) ||
            it.slogans.any { slogan -> slogan.text.lowercase().contains(q) }
        }
    }
}
