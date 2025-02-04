package lotto;

import lotto.domain.LottoGame;
import lotto.domain.LottoTicket;
import lotto.domain.Money;
import lotto.domain.TicketCheckResult;
import lotto.dto.LottoResult;
import lotto.dto.LottoResultItem;
import lotto.dto.LottoWin;

import java.util.ArrayList;
import java.util.List;

public class LottoVendingMachine {

    public static final int PRICE_PER_GAME = 1000;
    private final LottoNumbersGenerator lottoNumbersGenerator;

    public LottoVendingMachine(LottoNumbersGenerator lottoNumbersGenerator) {
        this.lottoNumbersGenerator = lottoNumbersGenerator;
    }

    public LottoTicket sellTicket(Money money) {
        return sellTicket(money, new ArrayList<>());
    }

    public LottoTicket sellTicket(Money money, List<LottoGame> manualLottoGames) {
        LottoTicket lottoTicket = new LottoTicket();
        lottoTicket.addAllGames(manualLottoGames);

        int autoNumberOfGames = money.numberOfGames(PRICE_PER_GAME) - manualLottoGames.size();
        for (int i = 0; i < autoNumberOfGames; i++) {
            lottoTicket.addGame(new LottoGame(lottoNumbersGenerator.generate()));
        }

        return lottoTicket;
    }

    public LottoResult check(LottoTicket ticket, LottoWin lottoWin) {
        TicketCheckResult result = ticket.check(lottoWin.getWinningNumbers(), lottoWin.getBonusNumber());
        List<LottoResultItem> items = result.mapLottoResultItemList(lottoWin);

        return new LottoResult(
                calculateRateOfReturn(ticket.moneyValue(PRICE_PER_GAME), items),
                items);
    }

    private String calculateRateOfReturn(int investment, List<LottoResultItem> items) {
        double totalProfit = items.stream()
                .mapToDouble(item -> (double)item.getPrizeMoney() * item.getCount())
                .reduce(0.0, Double::sum);

        return String.format("%.2f", totalProfit / investment);
    }
}
