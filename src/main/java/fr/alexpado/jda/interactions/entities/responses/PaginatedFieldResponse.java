package fr.alexpado.jda.interactions.entities.responses;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.function.Supplier;

public class PaginatedFieldResponse extends PaginatedResponse<MessageEmbed.Field> {

    public PaginatedFieldResponse(Supplier<EmbedBuilder> embedSupplier, List<MessageEmbed.Field> fields, int itemPerPage) {

        super(embedSupplier, fields, itemPerPage);
    }

    @Override
    public void render(EmbedBuilder embed, List<MessageEmbed.Field> items) {

        items.forEach(embed::addField);
    }

}
