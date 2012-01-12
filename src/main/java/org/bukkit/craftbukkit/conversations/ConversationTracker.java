package org.bukkit.craftbukkit.conversations;

import org.bukkit.conversations.Conversation;

import java.util.Deque;
import java.util.LinkedList;

/**
 */
public class ConversationTracker {

    private Deque<Conversation> conversationQueue = new LinkedList<Conversation>();

    public synchronized void beginConversation(Conversation conversation) {
        if (conversationQueue.contains(conversation)) {
            throw new IllegalArgumentException("Cannot begin the exact same Conversation object twice!");
        } else {
            conversationQueue.addLast(conversation);
        }
    }

    public synchronized void abandonConversation(Conversation conversation) {
        if (conversationQueue.contains(conversation)) {
            conversationQueue.remove(conversation);
        }
    }

    public synchronized void acceptConversationInput(String input) {
        if (isConversing()) {
            conversationQueue.getFirst().acceptInput(input);
        }
    }

    public synchronized boolean isConversing() {
        return !conversationQueue.isEmpty();
    }

    public synchronized boolean isConversingModaly() {
        return isConversing() && conversationQueue.getFirst().isModal();
    }
}
