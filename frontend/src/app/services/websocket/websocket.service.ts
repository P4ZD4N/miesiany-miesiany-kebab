import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { OrderResponse } from '../../responses/responses';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient: Client;
  private orderStatusSubject: Subject<OrderResponse> =
    new Subject<OrderResponse>();
  private orderRemovedSubject: Subject<number> = new Subject<number>();

  constructor() {
    this.stompClient = new Client({
      brokerURL: 'http://localhost:8080/ws',
      webSocketFactory: () => {
        return new SockJS('http://localhost:8080/ws');
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => this.subscribeToOrderUpdates(),
    });

    this.stompClient.activate();
  }

  private subscribeToOrderUpdates(): void {
    this.stompClient.subscribe('/topic/orders/status', (message: IMessage) => {
      const order: OrderResponse = JSON.parse(message.body);
      this.orderStatusSubject.next(order);
    });

    this.stompClient.subscribe('/topic/orders/removed', (message: IMessage) => {
      const removedOrder = JSON.parse(message.body);
      this.orderRemovedSubject.next(removedOrder);
    });
  }

  public onOrderStatusUpdate(): Observable<OrderResponse> {
    return this.orderStatusSubject.asObservable();
  }

  public onOrderRemoved(): Observable<number> {
    return this.orderRemovedSubject.asObservable();
  }
}
