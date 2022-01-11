package fr.alexpado.jda.interactions.entities.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Button;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

public class PaginatedResponse implements InteractionResponse {


    private final Supplier<EmbedBuilder>   supplier;
    private final List<MessageEmbed.Field> fields;
    private final LocalDateTime            time;
    private final int                      itemPerPage;
    private final int                      totalPage;
    private       int                      page;

    public PaginatedResponse(Supplier<EmbedBuilder> embedSupplier, List<MessageEmbed.Field> fields, int itemPerPage) {

        this.supplier = embedSupplier;
        this.fields   = fields;
        this.time     = LocalDateTime.now();

        this.page      = 1;
        this.itemPerPage = itemPerPage;
        this.totalPage = fields.size() / this.itemPerPage + Math.min(1, fields.size() % this.itemPerPage);
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }

    private List<MessageEmbed.Field> getPageItems() {

        int startIndex = (this.page - 1) * this.itemPerPage;
        int endIndex   = Math.min(this.page * this.itemPerPage, this.fields.size());
        return this.fields.subList(startIndex, endIndex);
    }


    @Override
    public EmbedBuilder getEmbed() {

        EmbedBuilder             embedBuilder = this.supplier.get();
        List<MessageEmbed.Field> pageItems    = this.getPageItems();
        pageItems.forEach(embedBuilder::addField);

        embedBuilder.setFooter(String.format("Page %s/%s", this.page, this.totalPage));

        return embedBuilder;
    }

    @Override
    public int getCode() {

        return 0;
    }

    public void previousPage() {

        if (this.page > 1) {
            this.page--;
        }
    }

    public void nextPage() {

        if (this.page < this.totalPage) {
            this.page++;
        }
    }

    public int getTotalPage() {

        return this.totalPage;
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

    public Button getPreviousButton(long responseId) {

        Button baseButton = Button.primary(String.format("page://%s/previous", responseId), "Previous");

        if (this.hasPreviousPage()) {
            return baseButton.asEnabled();
        } else {
            return baseButton.asDisabled();
        }
    }

    public Button getNextButton(long responseId) {

        Button baseButton = Button.primary(String.format("page://%s/next", responseId), "Next");

        if (this.hasNextPage()) {
            return baseButton.asEnabled();
        } else {
            return baseButton.asDisabled();
        }
    }


}
