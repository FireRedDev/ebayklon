import { IOffer } from 'app/shared/model/offer.model';

export interface IAuction {
  id?: number;
  auctionDescription?: string | null;
  auctionNames?: IOffer[] | null;
}

export const defaultValue: Readonly<IAuction> = {};
