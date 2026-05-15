package com.preshan.grocerylist.data.seed

data class SeedCategory(
    val name: String,
    val sortOrder: Int
)

data class SeedStoreType(
    val name: String,
    val sortOrder: Int
)

data class SeedItem(
    val name: String,
    val categoryName: String,
    val storeTypeName: String?
)

object DefaultSeedData {
    const val SEED_VERSION_KEY = "seed_version"
    const val SEED_VERSION_VALUE = "v4"

    val categories = listOf(
        SeedCategory("Food & Grocery", 1),
        SeedCategory("Vegetables", 2),
        SeedCategory("Fruits", 3),
        SeedCategory("Meat Shop", 4),
        SeedCategory("Health & Pharmacy", 5),
        SeedCategory("Household & Personal Care", 6)
    )

    val storeTypes = listOf(
        SeedStoreType("Supermarket", 1),
        SeedStoreType("Vegetable Shop", 2),
        SeedStoreType("Pharmacy", 3)
    )

    val items = listOf(
        // 1. Food & Grocery
        SeedItem("සම්බ සහල්", "Food & Grocery", "Supermarket"),
        SeedItem("රතු හාල්", "Food & Grocery", "Supermarket"),
        SeedItem("පාන් පිටි", "Food & Grocery", "Supermarket"),
        SeedItem("පරිප්පු", "Food & Grocery", "Supermarket"),
        SeedItem("සෝයා මීට්", "Food & Grocery", "Supermarket"),
        SeedItem("සීනි", "Food & Grocery", "Supermarket"),
        SeedItem("යෝගට්", "Food & Grocery", "Supermarket"),
        SeedItem("යෝගට් බීම", "Food & Grocery", "Supermarket"),
        SeedItem("බටර්", "Food & Grocery", "Supermarket"),
        SeedItem("ක්‍රීම්", "Food & Grocery", "Supermarket"),
        SeedItem("දියර කිරි", "Food & Grocery", "Supermarket"),
        SeedItem("කිරිපිටි", "Food & Grocery", "Supermarket"),
        SeedItem("අයිස් ක්‍රීම්", "Food & Grocery", "Supermarket"),
        SeedItem("මිරිස් කෑලි", "Food & Grocery", "Supermarket"),
        SeedItem("මිරිස් කුඩු", "Food & Grocery", "Supermarket"),
        SeedItem("බැදපු තුනපහ", "Food & Grocery", "Supermarket"),
        SeedItem("අමු තුනපහ", "Food & Grocery", "Supermarket"),
        SeedItem("කහ කුඩු", "Food & Grocery", "Supermarket"),
        SeedItem("අබ", "Food & Grocery", "Supermarket"),
        SeedItem("උළුහාල්", "Food & Grocery", "Supermarket"),
        SeedItem("ගම්මිරිස්", "Food & Grocery", "Supermarket"),
        SeedItem("සෝස්", "Food & Grocery", "Supermarket"),
        SeedItem("සෝයා සෝස්", "Food & Grocery", "Supermarket"),
        SeedItem("තල තෙල්", "Food & Grocery", "Supermarket"),
        SeedItem("පොල්තෙල්", "Food & Grocery", "Supermarket"),
        SeedItem("ජෙලි", "Food & Grocery", "Supermarket"),
        SeedItem("කස්ටඩ්", "Food & Grocery", "Supermarket"),
        SeedItem("ආමන්ඩ් ඇට", "Food & Grocery", "Supermarket"),
        SeedItem("පිස්ටා ඇට", "Food & Grocery", "Supermarket"),
        SeedItem("කැකර්ස්", "Food & Grocery", "Supermarket"),
        SeedItem("මුරුක්කු", "Food & Grocery", "Supermarket"),
        SeedItem("බයිට්", "Food & Grocery", "Supermarket"),
        SeedItem("පපඩම්", "Food & Grocery", "Supermarket"),
        SeedItem("බිස්කට්", "Food & Grocery", "Supermarket"),
        SeedItem("උම්බලකඩ", "Food & Grocery", "Supermarket"),
        SeedItem("හාල්මැස්සෝ", "Food & Grocery", "Supermarket"),
        SeedItem("වියළි ඉස්සන්", "Food & Grocery", "Supermarket"),
        // 2. Vegetables
        SeedItem("අල", "Vegetables", "Vegetable Shop"),
        SeedItem("ලූණු", "Vegetables", "Vegetable Shop"),
        SeedItem("රතු ලූණු", "Vegetables", "Vegetable Shop"),
        SeedItem("බී ලූණු", "Vegetables", "Vegetable Shop"),
        SeedItem("තක්කාලි", "Vegetables", "Vegetable Shop"),
        SeedItem("වම්බටු", "Vegetables", "Vegetable Shop"),
        SeedItem("බෝංචි", "Vegetables", "Vegetable Shop"),
        SeedItem("වට්ටක්කා", "Vegetables", "Vegetable Shop"),
        SeedItem("අමුමිරිස්", "Vegetables", "Vegetable Shop"),
        SeedItem("ඉඟුරු", "Vegetables", "Vegetable Shop"),
        SeedItem("සුදු ලූණු", "Vegetables", "Vegetable Shop"),
        SeedItem("එළවළු", "Vegetables", "Vegetable Shop"),
        // 3. Fruits
        SeedItem("පළතුරු", "Fruits", "Vegetable Shop"),
        SeedItem("කෙසෙල්", "Fruits", "Vegetable Shop"),
        SeedItem("අඹ", "Fruits", "Vegetable Shop"),
        SeedItem("දොඩම්", "Fruits", "Vegetable Shop"),
        SeedItem("අලිගැට පේර", "Fruits", "Vegetable Shop"),
        SeedItem("කොමඩු", "Fruits", "Vegetable Shop"),
        SeedItem("රට ඉඳි", "Fruits", "Vegetable Shop"),
        // 4. Meat Shop
        SeedItem("බිත්තර", "Meat Shop", "Supermarket"),
        SeedItem("මස්", "Meat Shop", "Supermarket"),
        SeedItem("මාළු", "Meat Shop", "Supermarket"),
        // 5. Health & Pharmacy
        SeedItem("පැනඩෝල්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("විටමින් C", "Health & Pharmacy", "Pharmacy"),
        SeedItem("කොලෙස්ටරෝල් බෙහෙත්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ඇස්පිරින්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("පානුබෙහෙත්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("අසමෝදගම්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ඩයිජීන්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ඇස් බෙහෙත් බින්දු", "Health & Pharmacy", "Pharmacy"),
        SeedItem("කොත්තමල්ලි පේයාව", "Health & Pharmacy", "Pharmacy"),
        // 6. Household & Personal Care
        SeedItem("සබන්", "Household & Personal Care", "Supermarket"),
        SeedItem("දත් බුරුසු", "Household & Personal Care", "Supermarket"),
        SeedItem("දත් බෙහෙත්", "Household & Personal Care", "Supermarket"),
        SeedItem("අත් සෝදන දියර", "Household & Personal Care", "Supermarket"),
        SeedItem("රේසර්", "Household & Personal Care", "Supermarket"),
        SeedItem("බල්ලන්ට දමන පවුඩර්", "Household & Personal Care", "Supermarket"),
        SeedItem("කිචන් ටවල් රෝල්ස්", "Household & Personal Care", "Supermarket"),
        SeedItem("හාපික් ටොයිලට් ක්ලීනර්", "Household & Personal Care", "Supermarket"),
        SeedItem("ලයිසෝල්", "Household & Personal Care", "Supermarket"),
        SeedItem("සබන් කුඩු", "Household & Personal Care", "Supermarket"),
        SeedItem("ෆැබ්‍රික් සොෆ්ට්නර්", "Household & Personal Care", "Supermarket"),
        SeedItem("Vim", "Household & Personal Care", "Supermarket"),
        SeedItem("කුණු බෑග්", "Household & Personal Care", "Supermarket"),
        SeedItem("ඉඳිආප්ප වට්ටි", "Household & Personal Care", "Supermarket"),
        SeedItem("ගිනිකූරු", "Household & Personal Care", "Supermarket"),
        SeedItem("ලයිටර්", "Household & Personal Care", "Supermarket"),
        SeedItem("මදුරු නාශක", "Household & Personal Care", "Supermarket"),
        SeedItem("රයිඩර්", "Household & Personal Care", "Supermarket"),
        // v4 additions — day-to-day items
        SeedItem("නූඩ්ල්ස්", "Food & Grocery", "Supermarket"),
        SeedItem("මැකරෝනි", "Food & Grocery", "Supermarket"),
        SeedItem("රොටි පිටි", "Food & Grocery", "Supermarket"),
        SeedItem("ආප්ප පිටි", "Food & Grocery", "Supermarket"),
        SeedItem("ඉඳිආප්ප පිටි", "Food & Grocery", "Supermarket"),
        SeedItem("බේකින් පවුඩර්", "Food & Grocery", "Supermarket"),
        SeedItem("වැනිලා", "Food & Grocery", "Supermarket"),
        SeedItem("ලුණු", "Food & Grocery", "Supermarket"),
        SeedItem("විනාකිරි", "Food & Grocery", "Supermarket"),
        SeedItem("ටොමැටෝ සෝස්", "Food & Grocery", "Supermarket"),
        SeedItem("මයෝනේස්", "Food & Grocery", "Supermarket"),
        SeedItem("මාගරින්", "Food & Grocery", "Supermarket"),
        SeedItem("චීස්", "Food & Grocery", "Supermarket"),
        SeedItem("ටින් මාළු", "Food & Grocery", "Supermarket"),
        SeedItem("පාන්", "Food & Grocery", "Supermarket"),
        SeedItem("ලීක්ස්", "Vegetables", "Vegetable Shop"),
        SeedItem("ගෝවා", "Vegetables", "Vegetable Shop"),
        SeedItem("කැරට්", "Vegetables", "Vegetable Shop"),
        SeedItem("බීට්රූට්", "Vegetables", "Vegetable Shop"),
        SeedItem("පිපිඤ්ඤා", "Vegetables", "Vegetable Shop"),
        SeedItem("කරවිල", "Vegetables", "Vegetable Shop"),
        SeedItem("පතෝල", "Vegetables", "Vegetable Shop"),
        SeedItem("මුරුංගා", "Vegetables", "Vegetable Shop"),
        SeedItem("අන්නාසි", "Fruits", "Vegetable Shop"),
        SeedItem("මිදි", "Fruits", "Vegetable Shop"),
        SeedItem("ඇපල්", "Fruits", "Vegetable Shop"),
        SeedItem("පේර", "Fruits", "Vegetable Shop"),
        SeedItem("දෙළුම්", "Fruits", "Vegetable Shop"),
        SeedItem("ලෙමන්", "Fruits", "Vegetable Shop"),
        SeedItem("කුකුල් මස්", "Meat Shop", "Supermarket"),
        SeedItem("සොසේජස්", "Meat Shop", "Supermarket"),
        SeedItem("බෑන්ඩේජ්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ප්ලාස්ටර්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("සැනිටයිසර්", "Health & Pharmacy", "Pharmacy"),
        SeedItem("මුව ආවරණ", "Health & Pharmacy", "Pharmacy"),
        SeedItem("උණ පරීක්ෂකය", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ORS", "Health & Pharmacy", "Pharmacy"),
        SeedItem("ෂැම්පු", "Household & Personal Care", "Supermarket"),
        SeedItem("කන්ඩිෂනර්", "Household & Personal Care", "Supermarket"),
        SeedItem("බොඩි වොෂ්", "Household & Personal Care", "Supermarket"),
        SeedItem("ඩියෝඩ්‍රන්ට්", "Household & Personal Care", "Supermarket"),
        SeedItem("ටිෂූ", "Household & Personal Care", "Supermarket"),
        SeedItem("ඩිෂ් වොෂ්", "Household & Personal Care", "Supermarket"),
        SeedItem("ස්පොන්ජ්", "Household & Personal Care", "Supermarket"),
        SeedItem("බ්ලීච්", "Household & Personal Care", "Supermarket"),
        SeedItem("කැරපොත්තන් නාශක", "Household & Personal Care", "Supermarket"),
        SeedItem("බල්බ්", "Household & Personal Care", "Supermarket"),
        SeedItem("බැටරි", "Household & Personal Care", "Supermarket"),
        SeedItem("ඉටිපන්දම්", "Household & Personal Care", "Supermarket")
    )
}
