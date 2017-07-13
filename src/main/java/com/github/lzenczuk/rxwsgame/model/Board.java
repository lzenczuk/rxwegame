package com.github.lzenczuk.rxwsgame.model;

import com.github.lzenczuk.rxwsgame.message.*;
import com.github.lzenczuk.rxwsgame.message.move.MoveCommand;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by dev on 12/07/17.
 */
public class Board {
    private PublishSubject<Observable<WSCommand>> input;
    private PublishSubject<WSEvent> output;
    private int x = 0;
    private int y = 0;

    public Board() {
        input = PublishSubject.create();

        output = PublishSubject.create();

        input.flatMap(wsCommandObservable -> wsCommandObservable).map(wsCommand -> {
            if(wsCommand instanceof MoveCommand){
                MoveCommand moveCommand = (MoveCommand) wsCommand;

                switch (moveCommand.getMoveType()){
                    case UP: if(y>0){
                        y=y-1;
                        return new UpdatePositionEvent(x,y);
                    }
                    case DOWN: if(y<512){
                        y=y+1;
                        return new UpdatePositionEvent(x,y);
                    }
                    case LEFT: if(x>0){
                        x=x-1;
                        return new UpdatePositionEvent(x,y);
                    }
                    case RIGHT: if(x<512){
                        x=x+1;
                        return new UpdatePositionEvent(x,y);
                    }
                    default:
                        return new EmptyEvent();
                }
            }else if(wsCommand instanceof TerminateCommand){
                return new TerminateEvent(((TerminateCommand) wsCommand).getClientId());
            }else{
                return new EmptyEvent();
            }
        }).filter(wsEvent -> !(wsEvent instanceof EmptyEvent)).subscribe(output);
    }

    public PublishSubject<Observable<WSCommand>> getInput() {
        return input;
    }

    public Observable<WSEvent> getOutput() {
        return output;
    }
}
