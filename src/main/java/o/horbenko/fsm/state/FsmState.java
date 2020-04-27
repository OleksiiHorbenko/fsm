/*
 * MIT License
 *
 * Copyright (c) 2020 Oleksii Horbenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * */
package o.horbenko.fsm.state;

import lombok.NonNull;
import o.horbenko.fsm.FsmStateHolder;
import o.horbenko.fsm.movement.FsmMovement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration of possible movements from FSM state. Contains {@link Map} of {@link FsmMovement}s by triggers.
 *
 * @param <S> State type
 * @param <T> Trigger type
 * @param <D> Data that holds state (implements {@link FsmStateHolder})
 * @author Oleksii Horbenko
 */
public class FsmState<S, T, D extends FsmStateHolder<S>> {

    private Map<T, FsmMovement<S, T, D>> possibleMovements;

    public FsmState() {
        this.possibleMovements = new HashMap<>();
    }

    public FsmState(@NonNull Map<T, FsmMovement<S, T, D>> possibleMovements) {
        this.possibleMovements = possibleMovements;
    }

    public FsmState<S, T, D> withMovement(@NonNull T onTrigger,
                                          FsmMovement<S, T, D> movement) {
        this.possibleMovements.put(onTrigger, movement);
        return this;
    }

    public Optional<FsmMovement<S, T, D>> getMovementByTrigger(T trigger) {
        if (trigger == null)
            return Optional.empty();

        return Optional.ofNullable(possibleMovements.get(trigger));
    }

}
