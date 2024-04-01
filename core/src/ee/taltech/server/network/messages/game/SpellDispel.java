package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.SpellTypes;

public class SpellDispel {
    public int id;

    /**
     * Construct SpellDispel message.
     * This will get rid of a spell.
     * @param id - spellId.
     */
    public SpellDispel (int id) {
        this.id = id;
    }
}
