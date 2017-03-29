package com.appcypher.wierd.phonedoctor;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import worldline.com.foldablelayout.FoldableLayout;

/**
 * Created by Nypro on 27-Mar-17.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

	private Context context;
	private List<PhoneData> phoneData;
	private Map<Integer, Boolean> foldStates = new HashMap<>();

	DetailAdapter(Context context, List<PhoneData> phoneData){
		this.context = context;
		this.phoneData = phoneData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new DetailAdapter.ViewHolder(new FoldableLayout(parent.getContext()));
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		// Bind Data
		PhoneData data = phoneData.get(position);
		holder.coverIcon.setImageResource(data.coverIconResource);
		holder.coverCategoryText.setText(data.coverCategory);
		holder.coverBriefDetail.setText(data.coverBriefDetail);

		holder.detailCategoryText.setText(data.coverCategory);
		holder.detailIcon.setImageResource(data.detailIconResource);
		for(int i = 0; i < data.detail.size(); i++){
			RelativeLayout detailBox = (RelativeLayout) LayoutInflater
					.from(context)
					.inflate(R.layout.detail_box, holder.detailLayout, false);

			final TextView title = (TextView) detailBox.getChildAt(0);
			if(data.detail.get(i).first!= "")
				title.setText("\u2022 " + data.detail.get(i).first);
			else title.setText("");

			final TextView detail = (TextView) detailBox.getChildAt(1);
			detail.setText(data.detail.get(i).second);

			holder.detailLayout.addView(detailBox);
		}

		// Bind States
		if (foldStates.containsKey(position)) {
			if (foldStates.get(position) == Boolean.TRUE) {
				if (!holder.foldableLayout.isFolded()) {
					holder.foldableLayout.foldWithoutAnimation();
				}
			} else if (foldStates.get(position) == Boolean.FALSE) {
				if (holder.foldableLayout.isFolded()) {
					holder.foldableLayout.unfoldWithoutAnimation();
				}
			}
		} else {
			holder.foldableLayout.foldWithoutAnimation();
		}

		holder.foldableLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.foldableLayout.isFolded()) {
					holder.foldableLayout.unfoldWithAnimation();
				} else {
					holder.foldableLayout.foldWithAnimation();
				}
			}
		});
		holder.foldableLayout.setFoldListener(new FoldableLayout.FoldListener() {
			@Override
			public void onUnFoldStart() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.foldableLayout.setElevation(5);
				}
			}

			@Override
			public void onUnFoldEnd() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.foldableLayout.setElevation(0);
				}
				foldStates.put(holder.getAdapterPosition(), false);
			}

			@Override
			public void onFoldStart() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.foldableLayout.setElevation(5);
				}
			}

			@Override
			public void onFoldEnd() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.foldableLayout.setElevation(0);
				}
				foldStates.put(holder.getAdapterPosition(), true);
			}
		});

	}

	@Override
	public int getItemCount() {
		return phoneData.size();
	}

	protected static class ViewHolder extends RecyclerView.ViewHolder{
		FoldableLayout foldableLayout;

		@Bind(R.id.cover_card)
		CardView coverCard;

		@Bind(R.id.cover_icon)
		ImageView coverIcon;

		@Bind(R.id.cover_category_text)
		TextView coverCategoryText;

		@Bind(R.id.cover_brief_detail)
		TextView coverBriefDetail;

		//---------------------

		@Bind(R.id.detail_card)
		CardView detailCard;

		@Bind(R.id.detail_layout)
		LinearLayout detailLayout;

		@Bind(R.id.detail_category_text)
		TextView detailCategoryText;

		@Bind(R.id.detail_icon)
		ImageView detailIcon;

		public ViewHolder(FoldableLayout foldableLayout) {
			super(foldableLayout);
			this.foldableLayout = foldableLayout;
			foldableLayout.setupViews(R.layout.item_cover, R.layout.item_details, R.dimen.card_cover_height, itemView.getContext());
			ButterKnife.bind(this, foldableLayout);
		}
	}

}
