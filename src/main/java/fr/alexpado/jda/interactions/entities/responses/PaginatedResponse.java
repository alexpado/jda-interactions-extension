package fr.alexpado.jda.interactions.entities.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

public abstract class PaginatedResponse<T> implements InteractionResponse {

    private final Supplier<EmbedBuilder> supplier;
    private final List<T>                fields;
    private final LocalDateTime          time;
    private final int                    itemPerPage;
    private final int                    totalPage;
    private       int                    page;

    public PaginatedResponse(Supplier<EmbedBuilder> embedSupplier, List<T> fields, int itemPerPage) {

        this.supplier = embedSupplier;
        this.fields   = fields;
        this.time     = LocalDateTime.now();

        this.page        = 1;
        this.itemPerPage = itemPerPage;
        this.totalPage   = fields.size() / this.itemPerPage + Math.min(1, fields.size() % this.itemPerPage);
    }

    @Override
    public final boolean isEphemeral() {

        return false;
    }

    public final List<T> getPageItems() {

        int startIndex = (this.page - 1) * this.itemPerPage;
        int endIndex   = Math.min(this.page * this.itemPerPage, this.fields.size());
        return this.fields.subList(startIndex, endIndex);
    }

    @Override
    public final EmbedBuilder getEmbed() {

        EmbedBuilder embedBuilder = this.supplier.get();
        List<T>      pageItems    = this.getPageItems();
        embedBuilder.setFooter(String.format("Page %s/%s", this.page, this.totalPage));
        this.render(embedBuilder, pageItems);
        return embedBuilder;
    }

    @Override
    public final int getCode() {

        return 0;
    }

    public final void previousPage() {

        if (this.page > 1) {
            this.page--;
        }
    }

    public final void nextPage() {

        if (this.page < this.totalPage) {
            this.page++;
        }
    }

    public boolean hasPreviousPage() {

        return this.page > 1;
    }

    public boolean hasNextPage() {

        return this.page < this.totalPage;
    }

    public LocalDateTime getTime() {

        return time;
    }

    public int getPage() {

        return page;
    }

    public int getItemPerPage() {

        return itemPerPage;
    }

    public int getTotalPage() {

        return totalPage;
    }

    public abstract void render(EmbedBuilder embed, List<T> items);

    public ActionRow[] getActionRows(String id) {

        return new ActionRow[]{
                ActionRow.of(
                        this.getPreviousButton(id),
                        this.getNextButton(id)
                )
        };
    }

    public Button getPreviousButton(String id) {

        Button baseButton = Button.primary(String.format("page://%s/previous", id), "Previous");

        if (this.hasPreviousPage()) {
            return baseButton.asEnabled();
        } else {
            return baseButton.asDisabled();
        }
    }

    public Button getNextButton(String id) {

        Button baseButton = Button.primary(String.format("page://%s/next", id), "Next");

        if (this.hasNextPage()) {
            return baseButton.asEnabled();
        } else {
            return baseButton.asDisabled();
        }
    }

}
