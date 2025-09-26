package com.example.kindvinapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kindvinapp.models.Challenge;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChallengeRepository {

    private static final String PREFS_NAME = "ChallengePrefs";
    private static final String CHALLENGES_KEY = "challenges";

    private static void saveChallenges(Context context, List<Challenge> challenges) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(challenges);
        editor.putString(CHALLENGES_KEY, json);
        editor.apply();
    }

    public static ArrayList<Challenge> getChallenges(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(CHALLENGES_KEY, null);

        if (json == null) {
            ArrayList<Challenge> defaultList = getDefaultChallenges();
            saveChallenges(context, defaultList);
            return defaultList;
        }

        Type type = new TypeToken<ArrayList<Challenge>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addChallenge(Context context, Challenge newChallenge) {
        ArrayList<Challenge> challenges = getChallenges(context);
        challenges.add(newChallenge);
        saveChallenges(context, challenges);
    }

    public static void updateChallenge(Context context, int position, Challenge updatedChallenge) {
        ArrayList<Challenge> challenges = getChallenges(context);
        if (position >= 0 && position < challenges.size()) {
            challenges.set(position, updatedChallenge);
            saveChallenges(context, challenges);
        }
    }

    public static void removeChallenge(Context context, int position) {
        ArrayList<Challenge> challenges = getChallenges(context);
        if (position >= 0 && position < challenges.size()) {
            challenges.remove(position);
            saveChallenges(context, challenges);
        }
    }

    public static ArrayList<Challenge> getDefaultChallenges() {
        ArrayList<Challenge> defaultList = new ArrayList<>();
        defaultList.add(new Challenge("Шарик в стаканчик", "Забрасывайте шарики в стаканчик по очереди. Хотя бы один шарик должен попасть в стаканчик. Но у вас есть всего 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Нащупай", "С завязанными глазами каждый должен определить на ощупь предмет, который ему положат в руку. Все должны дать ОДИН правильный ответ. Сделайте вашу ставку от 1 до 6."));
        defaultList.add(new Challenge("Лопнуть шар", "Игрокам необходимо за 15 секунд лопнуть шар над головой с помощью насоса. Сделайте вашу ставку от 1 до 6."));
        defaultList.add(new Challenge("Стакан", "Станцуйте со стаканом воды в руке, не проливая воду в течении 30 секунд. Участвует только капитан команды. Сделайте вашу ставку от 1 до 6."));
        defaultList.add(new Challenge("Удержать шарик", "Вы должны удержать воздушный шарик в воздухе отбивая его ладонями в течении 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Воздушный гольф", "Вам нужно по очереди сдувать шарик в стаканчик. Если хотя бы один шарик попал в стаканчик, вы выиграли. У вас 30 секунд на это задание. Сделайте вашу ставку от 1 до 6."));
        defaultList.add(new Challenge("Простой Флип", "Установите стаканчики на край стола. У вас есть 30 секунд, чтобы перевернуть хотя бы один стаканчик подкидывая снизу. Делайте это по очереди. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Качай", "Поднимите одного из игроков вашей команды как можно повыше 5 раз. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Встать со стаканчиком", "Вся команда, лёжа на полу удерживают стаканчик на лбу без помощи рук. На счёт 3 нужно всем встать, не уронив стаканчик. Хотя бы 1 стаканчик должен не упасть. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Жгучие танцы", "Танцевать джасденс с экрана. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Шарик с отскоком", "Поставьте стаканчик на стол. Забросьте шарик в стаканчик с отскоком. Должен попасть хотя бы один шарик. Делайте это по очереди. У вас 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Угадай мелодию перевертыш", "Угадайте мелодию-перевертыш. У вас есть 3 попытки. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Выдуть шарики", "Поставьте в ряд стаканчики с шариками внутри. У вас 30 секунд, чтобы выдуть все шарика из стаканчиков. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Воздушный шарик стаканчики", "Сдуйте с помощью воздушных шариков все стаканчики со стола. Время ограничено - 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Присесть со стаканчиком", "Поставьте стаканчик на голову. Вам нужно присесть 5 раз, не уронив стаканчик. Хотя бы у одного стаканчик должен остаться на голове. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Танец Бумага", "Танцуя разорвите листы бумаги ногами. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Вода ложки", "Опустошите миску с водой, используя только ложки. Нужно выпить воду. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Шарики вытряхнуть коробка", "Вытряхните все шарики из коробки танцуя. В каждой коробке может остаться не больше 3-х шариков. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Сдуть карточки", "Нужно сдуть только открытые карты. Остальные, которые лежат рубашкой вверх, должны остаться на столе. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Шарик в ложке", "Вся команда должна перенести шарики в ложках за линию финиша (устанавливает ведущий). Ни один шарик не должен упасть. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Допеть песню", "Допойте песню, которая внезапно оборвется. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Мостик", "Вся команда должна встать на мостик и простоять так в течении 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Шарик трубочка", "Все берут по одной трубочке. Нужно через трубочку втянуть шарик и перенести его в стаканчик. Все шарики должны попасть в стаканчик. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Угадай эмодзи", "Угадайте песню зашифрованную в эмодзи. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Карточка шляпа", "Кидая по очереди карты, попадите хотя бы одной в шляпу. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        defaultList.add(new Challenge("Слова на букву Ч", "Каждый участник из команды по очереди называет слово на букву \"Ч\". Должно прозвучать 10 слов. Повторяться нельзя. У вас есть 30 секунд. Сделайте ставку от 1 до 6."));
        return defaultList;
    }
}

